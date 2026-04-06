package com.github.shangtanlin.common.utils;

public class OrderStatusUtil {
    public static String getDesc(Integer status) {
        if (status == null) return "未知状态";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货"; // 已发货
            case 3: return "交易成功";
            case 4: return "订单已关闭";
            default: return "未知状态";
        }
    }
}
