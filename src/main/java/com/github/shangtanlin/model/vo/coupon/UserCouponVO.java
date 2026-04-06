package com.github.shangtanlin.model.vo.coupon;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCouponVO {
    private Long id;              // 领取记录ID
    private Long templateId;      // 模板ID
    private String title;         // 优惠券标题
    private String subTitle;      // 副标题
    private BigDecimal reduceAmount; // 减免金额
    private BigDecimal thresholdAmount; // 门槛金额
    private Integer status;       // 状态：0-未使用, 1-已锁定, 2-已使用, 3-已过期
    private String startTime;     // 生效时间（格式化后）
    private String endTime;       // 失效时间（格式化后）

    // --- 建议新增的字段 ---
    private String thresholdDesc; // 门槛描述，例如：“满100元可用”
    private Integer expireDays;   // 距离失效天数：0表示今天过期，-1表示已过期，>0表示剩余天数
    private String expireDesc;    // 失效文案，例如：“仅剩3小时” 或 “3天后过期”
    // --------------------


    private Boolean available; // 是否可用：true-可用，false-不可用
    private String reason;    // 不可用原因（例如：“金额未达标，还差10元”）
}
