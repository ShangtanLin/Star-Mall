package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
@Data
public class SkuVO {
    private Long skuId;

    private String skuTitle;           // 例如：黑色 42
    private String image;

    private BigDecimal price;
    private Integer stock;

    /** 这个 SKU 的属性组合，例如：{颜色=黑色, 尺码=42} */
    private Map<String, String> specs;
}
