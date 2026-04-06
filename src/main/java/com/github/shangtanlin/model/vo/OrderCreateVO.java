package com.github.shangtanlin.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateVO {

    // 1. 订单唯一标识（业务号）
    private String orderSn;

    // 2. 应付金额（落库后的最终法定金额，前端支付以此为准）
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal payAmount;

    // 3. 支付截止时间（建议返回毫秒时间戳，方便前端直接做倒计时计算）
    private Long payDeadline;


    // 4. 支付类型（透传预下单的选择，方便支付页直接勾选图标）
    private Integer paymentType;


    // 新增提示字段
    private Boolean priceChanged;    // 标记金额是否发生变动
    private String changeReason;     // 变动原因描述，如："由于活动结束，价格已更新，请重新核对"
}