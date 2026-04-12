package com.github.shangtanlin.common.constant;

public class RedisConstant {
    public static final String LOGIN_CODE_KEY = "user:login:code";
    public static final String REGISTER_CODE_KEY = "user:register:code";


    public static final String LOGIN_TOKEN_KEY = "login:token:";

    public static final Long CODE_TTL = 5L;

    public static final Long TOKEN_TTL = 30L;

    /** 优惠券库存 Key 前缀。格式：coupon:stock:{templateId} */
    public static final String COUPON_STOCK_KEY = "coupon:stock:";

    /** 已领券用户集合 Key 前缀 (Set结构)。格式：coupon:users:{templateId} */
    public static final String COUPON_USERS_KEY = "coupon:users:";

    /** * 💡 进阶建议：增加一个“秒杀快照”Key
     * 用于预热时存储模板的基本信息（有效期、门槛等），防止秒杀时回表查询数据库
     */
    public static final String COUPON_TEMPLATE_CACHE = "coupon:template:";


    public static final String COUPON_RESULT_KEY = "seckill:result:";

    public static final String CATEGORY_CACHE_KEY = "category";

    public static final String CART_CACHE_KEY = "cart:user:";

    public static final Long CART_TTL = 30L;

    public static final String SPU_SALES_RANK_KEY = "spu:sales:rank";


    public static final String ORDER_SUBMIT_KEY  = "order:submit:token:";




    public static final String CART_EMPTY_STUB = "EMPTY";

    // SKU 布隆过滤器 Key 名
    public static final String SKU_BLOOM_FILTER_KEY = "sku:bloom:filter";
}
