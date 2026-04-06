package com.github.shangtanlin.model.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规定券和类目/商品的底层对应关系
 */
@Data
@TableName("coupon_scope")
public class CouponScope {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long templateId; // 关联的优惠券模板ID

    /** 范围类型：1-指定类目, 2-指定商品, 3-指定品牌 */
    private Integer scopeType;

    /** 对应的目标ID：根据scopeType存入 类目ID、商品ID 或 品牌ID */
    private Long scopeId;

    private LocalDateTime createTime; // 创建时间
}
