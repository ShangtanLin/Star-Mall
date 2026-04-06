package com.github.shangtanlin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubOrderDetailVO {
    // --- 订单基础信息
    private Long subOrderId;
    private String subOrderSn;

    private Integer status; // 0-待支付, 1-已支付, 4-已关闭等
    private String statusDesc; // 状态描述，例如 "待付款" (方便前端直接显示)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; //订单创建时间


    // --- 收货人信息 ---
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress; // 详细地址：省市区 + 街道

    // --- 支付/时间信息 ---
    private String paymentTypeDesc; // 支付方式：微信、支付宝、积分


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime paymentTime; //支付时间


    // --- 费用明细 ---
    private BigDecimal goodsAmount;  // 商品总价
    private BigDecimal freightAmount;  // 运费
    private BigDecimal couponAmount;   // 优惠券抵扣
    private BigDecimal payAmount;   // 实付金额



    // --- 核心：订单关联的商品明细 ---
    // 这个列表对应数据库里的 order_items表
    private List<OrderItemVO> items;

    /**
     * 简单的状态码转文字逻辑
     * 也可以在 Service 层处理
     */
    public String getStatusDesc() {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已关闭";
            default -> "未知状态";
        };
    }

    // 当 Service 调用 setPaymentType 时，自动同步更新 Desc
    public void setPaymentTypeDesc(Integer paymentType) {
        if (paymentType == null) {
            this.paymentTypeDesc = "未支付";
        } else {
            this.paymentTypeDesc = switch (paymentType) {
                case 1 -> "支付宝";
                case 2 -> "微信支付";
                case 3 -> "银行卡";
                case 4 -> "货到付款";
                default -> "其他方式";
            };
        }
    }

}
