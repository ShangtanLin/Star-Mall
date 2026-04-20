package com.github.shangtanlin.model.entity.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Spu {

    private Long id;

    private Long shopId; // 新增，商户id

    private String name;

    private String description;

    private Long categoryId;

    private Long brandId;

    private String mainImage;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

    private Integer status;

    //将销量字段放到spu内，便于商品列表展示
    private Integer sales;

    //增加minPrice字段，便于商品列表展示
    private BigDecimal minPrice;
}
