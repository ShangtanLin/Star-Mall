package com.github.shangtanlin.common.constant.coupon;

public class CouponConstants {
    /** 优惠券类型：1-满减券 */
    public static final int TYPE_FULL_REDUCTION = 1;
    /** 优惠券类型：2-无门槛券 */
    public static final int TYPE_NO_THRESHOLD = 2;


    // ======== 数据库模板表状态 (status 字段) ========

    /** 模板状态：1-进行中 (正常发放中) */
    public static final int TEMPLATE_STATUS_PROCESSING = 1;

    /** 模板状态：2-已下架 (商家手动停止) */
    public static final int TEMPLATE_STATUS_OFFLINE = 2;

    /** 模板状态：3-已结束 (活动时间已到) */
    public static final int TEMPLATE_STATUS_ENDED = 3;


    // ======== 前端展示层状态 (receiveStatus 字段) ========

    /** 0-立即抢 */
    public static final int VO_STATUS_CAN_TAKE = 0;
    /** 1-已抢光 */
    public static final int VO_STATUS_EMPTY = 1;
    /** 2-去使用 */
    public static final int VO_STATUS_HAS_RECEIVED = 2;

    // ======== 用户领券记录表状态 (coupon_user_record.status) ========

    /** 记录状态：0-未使用 (初始领取状态，可用于下单抵扣) */
    public static final int USER_RECORD_STATUS_UNUSED = 0;

    /** 记录状态：1-已锁定 (用户下单后暂扣，等待支付中；若订单取消则回滚为0) */
    public static final int USER_RECORD_STATUS_LOCKED = 1;

    /** 记录状态：2-已使用 (订单支付成功，该券已核销，生命周期结束) */
    public static final int USER_RECORD_STATUS_USED = 2;

    /** 记录状态：3-已过期 (券已过有效期，不可再用) */
    public static final int USER_RECORD_STATUS_EXPIRED = 3;


    // ======== 优惠券有效期类型 (coupon_template.valid_type) ========

    /** 有效期类型：1-固定时间段 (例如：2026-03-01 至 2026-03-15) */
    public static final int VALID_TYPE_FIXED = 1;

    /** 有效期类型：2-领取后N天有效 (例如：领取后 7 天内有效) */
    public static final int VALID_TYPE_DAYS = 2;



    // lua脚本执行结果状态码解析常量
    public static final Long SUCCESS = 0L;
    public static final Long EMPTY_STOCK = 1L;
    public static final Long DUPLICATE_RECEIVE = 2L;
    public static final Long NOT_INITIALIZED = -1L;



}