package com.github.shangtanlin.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long skuId;

    private Integer quantity;

    private Integer checked;
}
