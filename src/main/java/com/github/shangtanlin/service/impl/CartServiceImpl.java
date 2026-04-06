package com.github.shangtanlin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.shangtanlin.common.utils.UserHolder;
import com.github.shangtanlin.mapper.CartItemMapper;
import com.github.shangtanlin.mapper.ShopMapper;
import com.github.shangtanlin.mapper.SkuMapper;
import com.github.shangtanlin.mapper.SpuMapper;
import com.github.shangtanlin.model.dto.CartItemDTO;
import com.github.shangtanlin.model.entity.cart.CartItem;
import com.github.shangtanlin.model.entity.product.Sku;
import com.github.shangtanlin.model.entity.product.Spu;
import com.github.shangtanlin.model.entity.shop.Shop;
import com.github.shangtanlin.model.redis.CartRedisJson;
import com.github.shangtanlin.model.vo.CartItemVO;
import com.github.shangtanlin.mq.cart.CartWriteBackMessage;
import com.github.shangtanlin.result.Result;
import com.github.shangtanlin.service.CartService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.shangtanlin.common.constant.CartConstant.*;
import static com.github.shangtanlin.common.constant.RedisConstant.CART_CACHE_KEY;
import static com.github.shangtanlin.config.mq.CartMQConfig.CART_EXCHANGE;
import static com.github.shangtanlin.config.mq.CartMQConfig.CART_ROUTING_KEY;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ShopMapper shopMapper;

    //获取购物车列表
    @Override
    public List<CartItemVO> getCartList() {
        //1.从redis中查询缓存
        Long userId = UserHolder.getUser().getId();
        String cartKey = CART_CACHE_KEY + userId;
        Map<Object, Object> carts = stringRedisTemplate.opsForHash().entries(cartKey); //key不存在时，返回的是空map
        //2.用户为新用户或购物车内容为空
        if (CollectionUtils.isEmpty(carts)) {  //carts为null或cart的值为空时都判true
            //检查数据库中是否也有空
            List<CartItem> cartItems = cartItemMapper.list(userId); //查询为空时，会返回空List
            if (cartItems.isEmpty()) {
                //返回空集合
                return Collections.emptyList();
            }
            //若数据库中不为空，则存入缓存
            Map<String,String>  cartMap = new HashMap<>();
            for (CartItem cartItem : cartItems) {
                //封装CartRedisJson对象
                CartRedisJson cartRedisJson = new CartRedisJson();
                cartRedisJson.setChecked(cartItem.getChecked());
                cartRedisJson.setQuantity(cartItem.getQuantity());
                cartRedisJson.setCreateTime(cartItem.getCreateTime());
                //将field和value存入map
                cartMap.put(cartItem.getSkuId().toString(), JSONUtil.toJsonStr(cartRedisJson));
            }
            //写入redis
            stringRedisTemplate.opsForHash().putAll(cartKey,cartMap);
            //设置ttl
            stringRedisTemplate.expire(cartKey,30, TimeUnit.DAYS);


            //数据库中购物车不为空，返回封装后的数据
            List<CartItemVO> cartItemVOS = cartItems.stream().map(
                    cartItem -> {
                        CartItemVO cartItemVO = new CartItemVO();
                        BeanUtil.copyProperties(cartItem, cartItemVO);
                        Sku sku = skuMapper.selectById(cartItem.getSkuId());
                        Spu spu = spuMapper.selectById(sku.getSpuId());
                        cartItemVO.setTitle(spu.getName() + spu.getDescription());
                        cartItemVO.setImage(spu.getMainImage());
                        cartItemVO.setUserId(userId);
                        cartItemVO.setSpuId(spu.getId());
                        cartItemVO.setSkuId(cartItem.getSkuId());
                        cartItemVO.setPrice(sku.getPrice());
                        cartItemVO.setSpecJson(sku.getSpecsJson());

                        //封装商家信息
                        cartItemVO.setShopId(spu.getShopId());
                        Shop shop = shopMapper.selectById(spu.getShopId());
                        cartItemVO.setShopName(shop.getShopName());
                        return cartItemVO;
                    }
            ).sorted(Comparator.comparing(CartItemVO::getCreateTime).reversed()) //按时间戳倒叙，时间戳大的在前面
                    .collect(Collectors.toList());

            return cartItemVOS;
        }

        //3.缓存中有数据
        List<CartItemVO> cartItemVOS =  carts.entrySet() //遍历每一个field-value
                .stream()
                .map(entry ->{
                    //1.获取field（skuId）
                    Long skuId = Long.valueOf(entry.getKey().toString());
                    //2.获取value（json字符串）
                    String json = entry.getValue().toString();
                    //3.对value里的数据进行清洗
                    // 3.1. 拦截空字符串、空格
                    if (StrUtil.isBlank(json)) {
                        // 记录日志：发现脏数据 cartKey field: entry.getKey()
                        return null;
                    }

                    // 3.2. 拦截 "null" 字符串 (视 JSON 库行为而定，防御性编程)
                    if ("null".equalsIgnoreCase(json)) {
                        return null;
                    }
                    CartItemVO cartItemVO = JSONUtil.toBean(json, CartItemVO.class);
                    //封装其他的数据
                    cartItemVO.setSkuId(skuId);
                    cartItemVO.setUserId(userId);
                    Sku sku = skuMapper.selectById(skuId);
                    cartItemVO.setSpecJson(sku.getSpecsJson());
                    cartItemVO.setPrice(sku.getPrice());
                    cartItemVO.setSpuId(sku.getSpuId());
                    Spu spu = spuMapper.selectById(sku.getSpuId());
                    cartItemVO.setImage(spu.getMainImage());
                    cartItemVO.setTitle(spu.getName() + " " + spu.getDescription());


                    //封装商家信息
                    cartItemVO.setShopId(spu.getShopId());
                    Shop shop = shopMapper.selectById(spu.getShopId());
                    cartItemVO.setShopName(shop.getShopName());

                    //返回CartItemVO
                    return cartItemVO;
                })
                .sorted(Comparator.comparing(CartItemVO::getCreateTime).reversed()) //按时间戳倒序，时间戳大的在前面
                .collect(Collectors.toList());

            return cartItemVOS;
        }


    //添加购物车
    @Override
    public Result<?> addToCart(CartItemDTO cartItemDTO) {
        //封装redis到json
        CartRedisJson cartRedisJson = new CartRedisJson();
        cartRedisJson.setCreateTime(LocalDateTime.now());
        cartRedisJson.setChecked(CART_DEFAULT_CHECKED);
        cartRedisJson.setQuantity(cartItemDTO.getQuantity());
        Long userId = UserHolder.getUser().getId();
        String cartKey = CART_CACHE_KEY + userId;
        stringRedisTemplate.opsForHash().put(cartKey,
                cartItemDTO.getSkuId().toString(),
                JSONUtil.toJsonStr(cartRedisJson));

        //写操作，刷新ttl
        stringRedisTemplate.expire(cartKey,30, TimeUnit.DAYS);

        //异步同步数据库
        //封装消息
        CartWriteBackMessage cartWriteBackMessage = new CartWriteBackMessage();
        cartWriteBackMessage.setCartRedisJson(cartRedisJson);
        cartWriteBackMessage.setSkuId(cartItemDTO.getSkuId());
        cartWriteBackMessage.setUserId(userId);
        cartWriteBackMessage.setType(WRITE_BACK_UPDATE);

        //给mq发送消息
        rabbitTemplate.convertAndSend(CART_EXCHANGE,
                CART_ROUTING_KEY,
                cartWriteBackMessage);//这里直接传message对象，避免冲突

        //返回结果
        return Result.ok();
    }

    //删除商品
    @Override
    public Result<?> deleteFromCart(Long skuId) {
        Long userId = UserHolder.getUser().getId();
        String cartKey = CART_CACHE_KEY + userId;
        Long result = stringRedisTemplate.opsForHash().delete(cartKey, skuId.toString());
        if (result == 0) {
            return Result.fail("商品不存在");
        }
        //写操作，刷新ttl
        stringRedisTemplate.expire(cartKey,30, TimeUnit.DAYS);
        //封装消息
        CartWriteBackMessage cartWriteBackMessage = new CartWriteBackMessage();
        cartWriteBackMessage.setUserId(userId);
        cartWriteBackMessage.setType(WRITE_BACK_DELETE);
        cartWriteBackMessage.setSkuId(skuId);
        //给mq发送消息
        rabbitTemplate.convertAndSend(CART_EXCHANGE,
                CART_ROUTING_KEY,
                cartWriteBackMessage);//这里直接传message对象，避免冲突

        return Result.ok();
    }

    //修改购物车（商品数量or选中状态）
    @Override
    public Result<?> updateCart(CartItemDTO cartItemDTO) {
        //为了保持原有的时间，先取出value，并转为CartRedisJson
        Long userId = UserHolder.getUser().getId();
        String cartKey = CART_CACHE_KEY + userId;
        String skuId = cartItemDTO.getSkuId().toString();
        String json = (String)stringRedisTemplate.opsForHash().get(cartKey, skuId);
        CartRedisJson cartRedisJson = JSONUtil.toBean(json, CartRedisJson.class); //反序列化，不能用BeanUtil
        cartRedisJson.setQuantity(cartItemDTO.getQuantity());
        cartRedisJson.setChecked(cartItemDTO.getChecked());
        cartRedisJson.setCreateTime(LocalDateTime.now());

        //2.重写覆盖
        stringRedisTemplate.opsForHash().put(cartKey,
                skuId,JSONUtil.toJsonStr(cartRedisJson));
        //写操作，刷新ttl
        stringRedisTemplate.expire(cartKey,30, TimeUnit.DAYS);

        //封装消息
        CartWriteBackMessage cartWriteBackMessage = new CartWriteBackMessage();
        cartWriteBackMessage.setCartRedisJson(cartRedisJson);
        cartWriteBackMessage.setSkuId(cartItemDTO.getSkuId());
        cartWriteBackMessage.setUserId(userId);
        cartWriteBackMessage.setType(WRITE_BACK_UPDATE);


        //给mq发送消息
        rabbitTemplate.convertAndSend(CART_EXCHANGE,
                CART_ROUTING_KEY,
                cartWriteBackMessage);//这里直接传message对象，避免冲突

        return Result.ok();
    }




}
