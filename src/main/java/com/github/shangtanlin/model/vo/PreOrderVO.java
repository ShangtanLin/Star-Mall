package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预下单时返回的确认数据
 */
@Data
public class PreOrderVO {
    private UserAddressVO defaultAddress;

    // --- 核心修改：按商家分组展示 ---
    private List<ShopGroupVO> shopGroups;

    private BigDecimal totalAmount;      // 所有商品原价总和
    private BigDecimal freightAmount;    // 所有商家运费之和
    private BigDecimal couponAmount;     // 总优惠金额
    private BigDecimal payAmount;        // 最终总实付

    private Integer totalQuantity;
    private List<Integer> supportPayTypes;

    /**
     * 新增内部类：按商家分组
     */
    @Data
    public static class ShopGroupVO {
        private Long shopId;
        private String shopName;
        private List<PreOrderItemVO> items; // 该商家下的商品列表

        private BigDecimal shopSubTotal;    // 该商家商品小计
        private BigDecimal shopFreight;     // 该商家的运费
        private BigDecimal shopCouponAmount; // 该商家分摊到的优惠
        private BigDecimal shopPayAmount;   // 该商家实付小计
    }

    @Data
    public static class PreOrderItemVO {
        private Long skuId;
        private Long shopId;
        private String skuName;
        private String spuName;
        private String spuImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subTotal;
        private BigDecimal couponAmount; // 【新增】单品分摊的优惠，方便前端展示“折后价”
        private Boolean hasStock;
    }
}