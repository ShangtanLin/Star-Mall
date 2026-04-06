package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartCardVO {
    private Long cartId;

    private Long userId;

    private String cartDescription; //商品描述，spu的name+description

    private String specsJson; //商品参数

    private String cartImage;

    private BigDecimal cartPrice;

    private Integer quantity;

}
