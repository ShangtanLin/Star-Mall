package com.github.shangtanlin.model.entity.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Sku {
    private Long id;

    private Long shopId; // 新增，商户id

    private Long spuId;

    /** SKU 标题，如 “黑色 42” */
    private String skuTitle;

    /** SKU 主图 */
    private String image;

    private BigDecimal price;

    private Integer stock;

    /** 规格组合 JSON，如 {"颜色":"黑色","尺码":"42"} */
    private String specsJson;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
