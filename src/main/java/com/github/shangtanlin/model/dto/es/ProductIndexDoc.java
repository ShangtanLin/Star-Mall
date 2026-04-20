package com.github.shangtanlin.model.dto.es;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductIndexDoc {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("shop_id")
    private Long shopId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("brand_id")
    private Long brandId;

    @JsonProperty("main_image")
    private String mainImage;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("update_time")
    private String updateTime;

    private Integer sales;

    @JsonProperty("min_price")
    private BigDecimal minPrice;
}
