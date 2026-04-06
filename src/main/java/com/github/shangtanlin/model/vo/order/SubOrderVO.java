package com.github.shangtanlin.model.vo.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubOrderVO {

    // 1. 订单基础信息
    private String parentOrderSn;      // 子订单编号

    private Long subOrderId;        // 子订单ID
    private String subOrderSn;      // 子订单编号

    private Integer status;         // 状态：1-待发货, 2-已发货, 3-已完成, 4-已关闭
    private String statusDesc;      // 状态描述 (如 "卖家已发货")，后端转好给前端

    //收货人信息
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverDetailAddress;

    // 2. 商家信息
    private Long shopId;
    private String shopName;
    private String shopLogo;

    // 3. 商品明细 (一个子订单下可能有多个商品)
    private List<OrderItemVO> items;

    // 4. 费用信息
    private BigDecimal goodsAmount;   // 商品原价总计
    private BigDecimal payAmount;     // 实际支付金额 (含运费、扣优惠)
    private BigDecimal freightAmount; // 运费
    private BigDecimal couponAmount;  // 订单优惠

    // 5. 物流信息 (仅已发货状态下有值)
    private String deliveryCompany;
    private String deliverySn;


    //6. 时间信息
    private LocalDateTime createTime;  //创建时间
    private LocalDateTime paymentTime; //支付时间
    private Integer paymentType; //支付方式
    private String paymentTypeDesc; //支付方式描述
    private LocalDateTime payDeadline; //支付截止时间



    private String remark; //订单备注

    //7.逻辑删除
    private Integer isDelete;

    /**
     * 内部类：商品明细
     */
    @Data
    public static class OrderItemVO {
        private Long spuId;
        private Long skuId;
        private String spuName;
        private String skuName;      // 规格：如“颜色:黑色;内存:256G”
        private String picUrl;
        private BigDecimal price;    // 下单时的单价
        private Integer quantity;    // 数量
    }


}
