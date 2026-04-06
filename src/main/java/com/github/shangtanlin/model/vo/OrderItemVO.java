package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderItemVO {
    // 商品 Sku ID
    private Long skuId;

    // 商品 Spu ID
    private Long spuId;

    // 店铺 ID
    private Long shopId;

    // 店铺 名称
    private String shopName;

    // 店铺 Logo
    private String shopLogo;


    // 商品Spu名称 (如: iPhone 15 Pro)
    private String spuName;

    // 商品Sku名称 (如：蓝色、41码)
    private String skuName;

    // 商品图片
    private String picUrl;

    // 下单时的单价
    private BigDecimal price;

    // 购买的数量
    private Integer quantity;

}