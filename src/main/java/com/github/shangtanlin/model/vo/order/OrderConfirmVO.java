package com.github.shangtanlin.model.vo.order;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVO {
    // --- 1. 用户选择的上下文（新增） ---
    private Long userAddressId;      // 当前选中的地址ID
    private Long couponUserRecordId;       // 当前选中的优惠券ID

    // --- 2. 费用汇总 (对应未来的 ParentOrder) ---
    private BigDecimal goodsAmount;      // 所有商品原价总和
    private BigDecimal freightAmount;    // 总运费
    private BigDecimal couponAmount;  // 全场满减/优惠券抵扣总额
    private BigDecimal payAmount;        // 最终应付总金额

    // --- 3. 按店铺拆分的子订单列表 (核心：前端按此循环渲染卡片) ---
    private List<ConfirmSubOrderVO> subOrders;

    /**
     * 子订单确认信息 (对应未来的一条 SubOrder 记录)
     */
    @Data
    public static class ConfirmSubOrderVO {
        private Long shopId;
        private String shopName;
        private String shopLogo;

        // 该店铺下的商品清单
        private List<ConfirmItemVO> items;

        // 该店铺的费用小计
        private BigDecimal subTotalAmount;   // 店铺内商品总价
        private BigDecimal subFreightAmount; // 该店运费
        private BigDecimal subCouponAmount; // 该店分摊到的优惠
        private BigDecimal subPayAmount;     // 该店小计应付
    }

    /**
     * 单个商品信息 (对应未来的 OrderItem)
     */
    @Data
    public static class ConfirmItemVO {
        private Long skuId;
        private Long spuId;
        private String spuName;
        private String skuName;
        private String picUrl;
        private BigDecimal price;    // 单价
        private Integer quantity;    // 数量
        private BigDecimal totalPrice; // price * quantity
    }

    private String submitToken; //防重Token
}
