package com.github.shangtanlin.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelMessage {
    private String orderSn;
    private Long userId;
}