package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCardVO {
    private Long spuId;

    private String spuName;

    private String mainImage;

    private String description;

    private BigDecimal price;

    private Integer sales;


}
