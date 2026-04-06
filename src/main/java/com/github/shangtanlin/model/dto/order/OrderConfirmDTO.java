package com.github.shangtanlin.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
public class OrderConfirmDTO {
    // --- 1. 外部影响因素 (全局) ---
    private Long addressId;            // 收货地址ID
    private Long couponUserRecordId;   // 平台通用优惠券/全场券ID

    // --- 2. 核心：结算项列表 ---
    // 支持多店铺、多商品的灵活组合
    private List<ShopOrderRequest> shops;

    /**
     * 按店铺组织的结算请求
     */
    @Data
    public static class ShopOrderRequest {
        private Long shopId;               // 店铺ID
        private Long shopCouponId;         // 该店铺选中的店铺券ID

        // 具体的商品项
        private List<ItemRequest> items;
    }

    /**
     * 单个商品项
     */
    @Data
    public static class ItemRequest {
        private Long skuId;
        private Integer quantity;
    }
}
