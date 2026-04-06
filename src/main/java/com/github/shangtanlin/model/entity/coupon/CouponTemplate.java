package com.github.shangtanlin.model.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 决定了“一张券长什么样、面额多少、谁能领、在哪用”
 */

@Data
@TableName("coupon_template")
public class CouponTemplate {
    @TableId(type = IdType.AUTO)
    private Long id; // 模板唯一主键

    private String title; // 优惠券名称（如：开学季满减券）
    private String subTitle; // 副标题（如：仅限数码类目使用）

    /** 优惠券类型：1-满减券（满100减20）, 2-无门槛券（立减5元） */
    private Integer couponType;

    /** 适用范围类型：1-全场通用, 2-指定类目可用, 3-指定商品可用 */
    private Integer scopeType;

    /** 冗余存储的范围ID：如类目ID或商品ID，逗号分隔（101,102），用于快速展示 */
    private String scopeValues;

    private BigDecimal thresholdAmount; // 使用门槛金额（如果是无门槛券，此值为0）
    private BigDecimal reduceAmount; // 优惠减免金额（减多少钱）

    private Integer publishCount; // 计划发行总数量
    private Integer receivedCount; // 已经被領取的数量（不能超过publishCount）

    /** 有效期类型：1-固定时间段（6.1-6.18）, 2-领取后N天有效（如注册赠券） */
    private Integer validType;
    private LocalDateTime useStartTime; // 【固定时间段专用】使用开始时间
    private LocalDateTime useEndTime; // 【固定时间段专用】使用结束时间
    private Integer validDays; // 【领取后N天专用】自领取之日起有效的天数

    private LocalDateTime publishStartTime; // 允许用户領取的开始时间
    private LocalDateTime publishEndTime; // 允许用户領取的结束时间（活动下架时间）

    /** 状态：1-进行中（正常显示）, 2-已下架（商家手动停止）, 3-已结束（时间到或发完） */
    private Integer status;
}