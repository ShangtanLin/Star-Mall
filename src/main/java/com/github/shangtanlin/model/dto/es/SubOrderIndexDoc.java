package com.github.shangtanlin.model.dto.es;

import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 对应 Elasticsearch 中 sub_order_index 索引的文档对象
 * 用于从 ES 检索数据并映射为 Java 对象
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;


/**
 * 对应 Elasticsearch 中 sub_order_index 索引的文档对象
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubOrderIndexDoc {

    //对应数据库中sub_order的主键,业务id
    @JsonProperty("sub_order_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long subOrderId;


    @JsonProperty("sub_order_sn")
    private String subOrderSn;

    @JsonProperty("parent_order_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentOrderId;

    @JsonProperty("parent_order_sn")
    private String parentOrderSn;

    @JsonProperty("user_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;  //核心过滤字段

    // 收货人信息
    @JsonProperty("receiver_name")
    private String receiverName;

    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @JsonProperty("receiver_province")
    private String receiverProvince;

    @JsonProperty("receiver_city")
    private String receiverCity;

    @JsonProperty("receiver_district")
    private String receiverDistrict;

    @JsonProperty("receiver_detail_address")
    private String receiverDetailAddress;



    // --- 3. 商家信息 ---
    @JsonProperty("shop_id") // 新增：对应 ES 中的 shop_id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shopId;

    @JsonProperty("shop_name")
    private String shopName;

    @JsonProperty("shop_logo") // 新增：对应 ES 中的 shop_logo
    private String shopLogo;

    // --- 4. 订单状态与时间 ---
    private Integer status;

    @JsonProperty("create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;



    @JsonProperty("pay_deadline")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payDeadline; //截至时间

    // --- 5. 金额与物流信息 (新增补全) ---
    @JsonProperty("goods_amount")
    private Double goodsAmount;

    @JsonProperty("coupon_amount")
    private Double couponAmount;

    @JsonProperty("pay_amount")
    private Double payAmount;

    @JsonProperty("freight_amount")
    private Double freightAmount;

    @JsonProperty("delivery_company")
    private String deliveryCompany;

    @JsonProperty("delivery_sn")
    private String deliverySn;

    @JsonProperty("payment_type")
    private Integer paymentType;


    @JsonProperty("payment_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;


    @JsonProperty("is_delete")
    private Integer isDelete;

    // --- 6. 商品明细 ---
    private List<ItemInnerDTO> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemInnerDTO {
        @JsonProperty("spu_id")
        private Long spuId;

        @JsonProperty("sku_id")
        private Long skuId;

        @JsonProperty("spu_name")
        private String spuName;

        @JsonProperty("sku_name")
        private String skuName;

        @JsonProperty("pic_url")
        private String picUrl;


        private Double price;

        private Integer quantity;

    }
}