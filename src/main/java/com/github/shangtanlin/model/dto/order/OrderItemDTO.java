package com.github.shangtanlin.model.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 订单项目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class OrderItemDTO {
    @NotNull(message = "商品SKU不能为空")
    private Long skuId;

    @NotNull(message = "购买数量不能为空")
    private Integer quantity;
}
