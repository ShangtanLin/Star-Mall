package com.github.shangtanlin.model.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * “用户手里有什么券，现在能不能用”
 */
@Data
@TableName("coupon_user_record")
public class CouponUserRecord {
    @TableId(type = IdType.AUTO)
    private Long id; // 用户券唯一ID（每个用户领一张就生成一个ID）

    private Long userId; // 领券的用户ID
    private Long templateId; // 关联的模板ID（通过它知道券的面额和规则）
    private String orderSn; // 如果券已使用，记录对应的订单号；未使用则为NULL

    /** 状态：0-未使用, 1-已锁定（已下单未支付）, 2-已使用（支付成功）, 3-已过期 */
    private Integer status;

    private LocalDateTime startTime; // 这张券具体的生效开始时间（由模板计算得出）
    private LocalDateTime endTime; // 这张券具体的失效结束时间（由模板计算得出）
    private LocalDateTime getTime; // 用户領取的时间
    private LocalDateTime useTime; // 用户核销（支付成功）的时间
}