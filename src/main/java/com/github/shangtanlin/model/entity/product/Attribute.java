package com.github.shangtanlin.model.entity.product;

import lombok.Data;

@Data
public class Attribute {

    private Long id;

    private Long spuId;

    private String attrName;

    private Integer sort;
}
