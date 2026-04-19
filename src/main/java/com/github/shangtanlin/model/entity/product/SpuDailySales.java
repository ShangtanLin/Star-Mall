package com.github.shangtanlin.model.entity.product;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日商品销量备份表
 */
@Data
public class SpuDailySales {

    private Long id;

    private Long spuId;

    private Integer sales;

    private LocalDate saleDate;

    private LocalDateTime createTime;
}