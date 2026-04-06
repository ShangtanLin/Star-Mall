package com.github.shangtanlin.model.entity.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long subOrderId; // 关联子订单ID

    private String subOrderSn; // 关联子订单编号

    private Long parentOrderId; // 冗余主订单ID

    private String parentOrderSn; // 冗余主订单编号

    private Long spuId;
    private Long skuId;
    private String spuName; // 商品名称快照
    private String skuName; // 规格快照（如：黑色 256G）
    private String picUrl; // 图片快照

    private BigDecimal price; // 下单单价
    private Integer quantity; // 数量

    private BigDecimal totalAmount; // 总额 (price * quantity)
    private BigDecimal couponAmount; // 该商品分摊的优惠
    private BigDecimal realAmount; // 最终分摊后的实付金额
}