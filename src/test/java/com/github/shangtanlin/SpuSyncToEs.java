package com.github.shangtanlin;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import com.github.shangtanlin.mapper.SpuMapper;
import com.github.shangtanlin.model.dto.es.ProductIndexDoc;
import com.github.shangtanlin.model.entity.product.Spu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
public class SpuSyncToEs {
    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private SpuMapper spuMapper;

    private static final String INDEX_NAME = "product_index";
    private static final DateTimeFormatter ES_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 将spu数据插入product_index
    @Test
    void syncAllspuToEs() throws Exception {
        // 1. 查询所有SPU
        List<Spu> spuList = spuMapper.selectList();
        System.out.println("从数据库查询到 " + spuList.size() + " 条SPU数据");

        // 2. 批量插入ES
        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

        for (Spu spu : spuList) {
            ProductIndexDoc doc = convertToDoc(spu);
            bulkBuilder.operations(op -> op
                    .index(idx -> idx
                            .index(INDEX_NAME)
                            .id(String.valueOf(spu.getId()))
                            .document(doc)
                    )
            );
        }

        // 3. 执行批量操作
        BulkResponse response = client.bulk(bulkBuilder.build());

        System.out.println("同步完成，成功：" + (response.errors() ? "有错误" : "全部成功"));
        System.out.println("共处理：" + response.items().size() + " 条记录");

        // 打印详细错误信息
        if (response.errors()) {
            response.items().forEach(item -> {
                if (item.error() != null) {
                    System.err.println("文档ID " + item.id() + " 失败: " + item.error().reason());
                }
            });
        }

    }



    private ProductIndexDoc convertToDoc(Spu spu) {
        ProductIndexDoc doc = new ProductIndexDoc();
        doc.setId(spu.getId());
        doc.setShopId(spu.getShopId());
        doc.setName(spu.getName());
        doc.setDescription(spu.getDescription());
        doc.setCategoryId(spu.getCategoryId());
        doc.setBrandId(spu.getBrandId());
        doc.setMainImage(spu.getMainImage());
        doc.setSales(spu.getSales());
        doc.setMinPrice(spu.getMinPrice());
        doc.setStatus(spu.getStatus());
        doc.setCreateTime(spu.getCreateTime().format(ES_DATE_FORMAT));
        doc.setUpdateTime(spu.getUpdateTime().format(ES_DATE_FORMAT));
        return doc;
    }


    // 删除product_index下的所有文档
    @Test
    void deleteAllDocs() throws Exception {
        DeleteByQueryResponse response = client.deleteByQuery(d -> d
                .index(INDEX_NAME)
                .query(q -> q.matchAll(m -> m))
        );

        System.out.println("删除完成，共删除：" + response.deleted() + " 条文档");
    }

}
