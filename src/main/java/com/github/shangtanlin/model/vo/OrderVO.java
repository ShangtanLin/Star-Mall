package com.github.shangtanlin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    // --- 订单基础信息
    private Long subOrderId;
    private String subOrderSn;

    private Integer status; // 0-待支付, 1-已支付, 4-已关闭等
    private String statusDesc; // 状态描述，例如 "待付款" (方便前端直接显示)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; //订单创建时间

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
}