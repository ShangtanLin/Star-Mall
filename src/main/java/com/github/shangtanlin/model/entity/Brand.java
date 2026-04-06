package com.github.shangtanlin.model.entity;

import lombok.Data;

@Data
public class Brand {

    private Long id;

    /** 品牌名称，如 Nike、Adidas */
    private String name;

    /** 品牌 Logo 图片链接 */
    private String logo;

    /** 品牌简介、描述 */
    private String description;

    /** 排序字段 */
    private Integer sort;
}
