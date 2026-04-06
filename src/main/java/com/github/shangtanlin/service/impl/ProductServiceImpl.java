package com.github.shangtanlin.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import co.elastic.clients.elasticsearch.core.search.HighlightField;


import co.elastic.clients.util.NamedValue;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.shangtanlin.model.dto.es.ProductDoc;
import com.github.shangtanlin.model.dto.ProductQueryDTO;
import com.github.shangtanlin.mapper.*;
import com.github.shangtanlin.model.entity.*;
import com.github.shangtanlin.model.entity.category.Category;
import com.github.shangtanlin.model.entity.product.*;
import com.github.shangtanlin.model.entity.shop.Shop;
import com.github.shangtanlin.result.PageResult;
import com.github.shangtanlin.service.ProductService;
import com.github.shangtanlin.model.vo.AttributeVO;
import com.github.shangtanlin.model.vo.ProductCardVO;
import com.github.shangtanlin.model.vo.ProductVO;
import com.github.shangtanlin.model.vo.SkuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.shangtanlin.common.constant.RedisConstant.SPU_SALES_RANK_KEY;

@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private AttributeMapper attributeMapper;
    @Autowired
    private AttributeValueMapper attributeValueMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ElasticsearchClient client;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ShopMapper shopMapper;

    //商品列表分页查询
    @Override //使用pagehelper插件进行分页查询
    public PageResult<ProductCardVO> list(ProductQueryDTO productQueryDTO) {
        //1.启动分页
        PageHelper.startPage(productQueryDTO.getPageNo(),productQueryDTO.getPageSize());
        //2.执行查询(PageHelper会自动在sql后面追加limit)
        List<Spu> spus = spuMapper.selectList();
        //3.使用pageInfo获取数据总数total
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        //4.将Spu封装为ProductVO
        List<ProductCardVO> voList = spus.stream()
                .map(this::buildProductCardVO)
                .collect(Collectors.toList());
        //5.返回封装后的结果
        return new PageResult<>(spuPageInfo.getTotal(),voList);
    }

    //查询商品详情
    @Override
    public ProductVO getProductById(Long spuId) {
        //查询spu
        Spu spu = spuMapper.selectById(spuId);
        //查询attribute
        List<Attribute> attributes = attributeMapper.selectBySpuId(spuId);
        //查询attributeValue
        List<AttributeValue> attributeValues = attributeValueMapper.selectBySpuId(spuId);
        //查询sku
        List<Sku> skus = skuMapper.selectBySpuId(spuId);
        return buildProductVO(spu,attributes,attributeValues,skus);
    }


    //搜索商品
    @Override
    public PageResult<ProductCardVO> searchProduct(String keyword, Long categoryId,
                                                   Integer pageNo, Integer pageSize) throws IOException {

        // 1. 构建查询条件
        Query query = Query.of(q -> q.bool(b -> {
            if (StringUtils.hasText(keyword)) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(keyword)
                        .fields("name", "description")
                ));
            }
            if (categoryId != null) {
                b.filter(f -> f.term(t -> t
                        .field("category_id")
                        .value(categoryId)
                ));
            }
            return b;
        }));

        // 2. 使用 9.x 最新的 NamedValue 方式构建 SearchRequest
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("product_index")
                .query(query)
                .from((pageNo - 1) * pageSize)
                .size(pageSize)
                .highlight(h -> h
                        .preTags("<em style='color:red;'>")
                        .postTags("</em>")
                        // --- 官方 9.1+ 推荐写法 ---
                        .fields(
                                NamedValue.of("name", HighlightField.of(hf -> hf)),
                                NamedValue.of("description", HighlightField.of(hf -> hf
                                        .fragmentSize(100)
                                        .numberOfFragments(1)))
                        )
                )
        );



        // 4. 执行搜索
        SearchResponse<ProductDoc> response = client.search(searchRequest, ProductDoc.class);

        //// 5. 结果映射 + 高亮处理
        List<ProductCardVO> voList = response.hits().hits().stream()
                .map(hit -> {
                    ProductDoc doc = hit.source();
                    ProductCardVO vo = new ProductCardVO();

                    // 默认数据
                    vo.setSpuId(doc.getId());
                    vo.setSpuName(doc.getName());
                    vo.setMainImage(doc.getMainImage());
                    vo.setDescription(doc.getDescription());
                    vo.setPrice(doc.getMinPrice());
                    vo.setSales(doc.getSales());

                    // ✅ 高亮处理
                    if (hit.highlight() != null && !hit.highlight().isEmpty()) {

                        if (hit.highlight().containsKey("name")) {
                            vo.setSpuName(hit.highlight().get("name").get(0));
                        }

                        if (hit.highlight().containsKey("description")) {
                            vo.setDescription(hit.highlight().get("description").get(0));
                        }
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(response.hits().total().value(), voList);

    }

    //获取今日热门商品
    @Override
    public List<ProductCardVO> getHotProductsToday() {
        String spuSalesKey = SPU_SALES_RANK_KEY;
        Set<String> top10SpuIds = stringRedisTemplate.opsForZSet().reverseRange(spuSalesKey, 0, 9);
        List<Long> ids = top10SpuIds.stream().map(Long::valueOf).collect(Collectors.toList());
        List<Spu> spus = spuMapper.selectByIds(ids);
        //对查询到的结果按zset返回的id排序
        List<Spu> afterSortSpu = new ArrayList<>();
        Map<Long, Spu> spuMap = spus.stream().collect(Collectors.toMap(Spu::getId, s -> s));
        for (Long id : ids) {
            Spu spu = spuMap.get(id);
            if (spu != null) {
                afterSortSpu.add(spu);
            }
        }
        List<ProductCardVO> productCardVOS = afterSortSpu.stream().map(spu -> {
            ProductCardVO productCardVO = buildProductCardVO(spu);
            return productCardVO;
        }).collect(Collectors.toList());
        return productCardVOS;
    }


    /**
     * 扣减库存
     * @param skuId
     * @param quantity
     */
    @Override
    public void deductStock(Long skuId, Integer quantity) {
        skuMapper.deductStock(skuId,quantity);
    }

    /**
     * 回补库存
     * @param skuId
     * @param quantity
     */
    public void rollbackStock(Long skuId, Integer quantity) {
        skuMapper.rollbackStock(skuId,quantity);
    }





    //封装商品详情页的ProductVO
    private ProductVO buildProductVO(Spu spu,List<Attribute> attributes
            ,List<AttributeValue> attributeValues,List<Sku> skus) {
        //1.封装spu基础信息
        ProductVO productVO = new ProductVO();
        productVO.setSpuId(spu.getId());
        productVO.setName(spu.getName());
        productVO.setDescription(spu.getDescription());
        Category category = categoryMapper.selectById(spu.getCategoryId());
        productVO.setCategoryName(category.getName());
        productVO.setCategoryId(spu.getCategoryId());
        productVO.setMainImage(spu.getMainImage());
        productVO.setBrandId(spu.getBrandId());
        Brand brand = brandMapper.selectById(spu.getBrandId());
        productVO.setBrandName(brand.getName());
        //2.封装属性与属性值
        Map<Long, List<AttributeValue>> valueMap =
                attributeValues.stream().collect(Collectors.groupingBy(AttributeValue::getAttrId));
        List<AttributeVO> attrVOList = attributes.stream().map(attr -> {
            AttributeVO attrVO = new AttributeVO();
            attrVO.setAttrName(attr.getAttrName());

            List<AttributeValue> valList = valueMap.get(attr.getId());
            if (valList != null) {
                List<String> vals = valList.stream()
                        .map(AttributeValue::getValue)
                        .collect(Collectors.toList());
                attrVO.setValues(vals);
            } else {
                attrVO.setValues(Collections.emptyList());
            }

            return attrVO;
        }).collect(Collectors.toList());
        productVO.setAttributes(attrVOList);

        //3.封装sku
        List<SkuVO> skuVOList = skus.stream().map(sku -> {
            SkuVO skuVO = new SkuVO();

            skuVO.setSkuId(sku.getId());
            skuVO.setSkuTitle(sku.getSkuTitle());
            skuVO.setImage(sku.getImage());
            skuVO.setPrice(sku.getPrice());
            skuVO.setStock(sku.getStock());

            // specs_json: JSON → Map<String, String>
            Map<String, String> specs = JSON.parseObject(sku.getSpecsJson(), Map.class);
            skuVO.setSpecs(specs);

            return skuVO;
        }).collect(Collectors.toList());
        productVO.setSkus(skuVOList);


        //封装店铺信息
        Shop shop = shopMapper.selectById(spu.getShopId());
        productVO.setShopId(shop.getId());
        productVO.setShopName(shop.getShopName());
        productVO.setShopLogo(shop.getShopLogo());
        productVO.setShopRating(shop.getRating());


        //4.返回封装好的productVO
        return productVO;
    }

    //封装商品列表页的ProductCardVO
    private ProductCardVO buildProductCardVO(Spu spu) {
        ProductCardVO vo = new ProductCardVO();
        vo.setSpuId(spu.getId());
        vo.setSpuName(spu.getName());
        vo.setMainImage(spu.getMainImage());
        vo.setPrice(spu.getMinPrice());
        vo.setDescription(spu.getDescription());
        vo.setSales(spu.getSales());
        return vo;
    }





}
