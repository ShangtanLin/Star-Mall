package com.github.shangtanlin.model.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 这个类是“账本”，解决一单多券时的金额分摊问题
 */
@Data
@TableName("order_coupon_settlement")
public class OrderCouponSettlement {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String order_sn; // 关联的订单编号
    private Long userCouponId; // 用户持有的那一张券记录ID
    private Long templateId; // 冗余模板ID，方便统计某类活动的总核销金额

    private BigDecimal settlementAmount; // 该优惠券在本项目中实际抵扣了多少钱（实扣金额）

    private LocalDateTime createTime; // 结算记录生成时间
}
