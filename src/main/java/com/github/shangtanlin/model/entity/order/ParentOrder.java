package com.github.shangtanlin.model.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 订单主表实体类
 * 对应数据库表：order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentOrder {

    /**
     * 主键ID (雪花算法生成)
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总金额（商品总价）
     */
    private BigDecimal goodsAmount;

    /**
     * 应付总金额（实际支付金额 = 总金额 - 优惠金额 + 运费）
     */
    private BigDecimal payAmount;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭
     */
    private Integer status;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 支付方式：1->支付宝；2->微信；3->银卡；4->货到付款
     */
    private Integer paymentType;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;


    /**
     * 支付截至时间
     */
    private LocalDateTime payDeadline;



    /**
     * 用户领取的优惠券记录ID (对应user_coupon表主键)
     */
    private Long couponUserRecordId;

    /**
     * 该订单实际抵扣的优惠券金额
     */
    private BigDecimal couponAmount;


    /**
     * 逻辑删除
     */
    private Integer isDelete;
}