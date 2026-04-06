package com.github.shangtanlin.service;


import com.github.shangtanlin.model.dto.CartItemDTO;
import com.github.shangtanlin.model.vo.CartItemVO;
import com.github.shangtanlin.result.Result;

import java.util.List;

public interface CartService {
    List<CartItemVO> getCartList();

    Result<?> addToCart(CartItemDTO cartItemDTO);

    Result<?> deleteFromCart(Long skuId);

    Result<?> updateCart(CartItemDTO cartItemDTO);
}
