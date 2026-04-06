package com.github.shangtanlin.model.vo.coupon;

import lombok.Data;
import java.math.BigDecimal;


@Data
public class CouponTemplateVO {
    /** 模板ID，点击领取时需要传回后端 */
    private Long id;

    /** 优惠券名称（主标题） */
    private String title;

    /** 副标题 */
    private String subTitle;

    /** 优惠券类型：1-满减, 2-无门槛 */
    private Integer couponType;

    /** 门槛金额 */
    private BigDecimal thresholdAmount;

    /** 减免金额 */
    private BigDecimal reduceAmount;

    /** * 格式化后的门槛文案
     * 例如："满100元可用" 或 "无门槛"
     */
    private String thresholdDesc;

    /** * 有效期展示文案
     * 例如："2026.03.01-2026.03.15" 或 "领取后7天内有效"
     */
    private String validityDesc;


    /** * 领取状态：0-立即抢, 1-已抢光, 2-去使用
     * 用于前端判断按钮显示样式
     */
    private Integer receiveStatus;
}