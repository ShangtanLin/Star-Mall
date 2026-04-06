package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.cart.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartItemMapper {

    @Select("select * from cart_item where user_id = #{userId}")
    List<CartItem> list(@Param("userId")Long userId);

    //@Update("update cart_item set " +
    //        "quantity = #{quantity}" +
    //        "checked = #{checked}" +
    //        "where sku_id = #{skuId}")
    //void updateBySkuId(Long skuId, CartRedisJson cartRedisJson);

    @Delete("delete from cart_item where user_id = #{userId} and sku_id = #{skuId}")
    int delete(@Param("userId") Long userId, @Param("skuId") Long skuId);

    @Delete("delete from cart_item where user_id = #{userId}")
    int deleteAll(@Param("userId") Long userId);


    @Insert("insert into cart_item (user_id,sku_id,quantity,checked)" +
            " values (#{userId},#{skuId},#{quantity},#{checked})")
    int insert(CartItem cartItem);


    /**
     * 匹配删除购物车
     * @param userId
     * @param skuIds
     * @return
     */
    int deleteByUserIdAndSkuIds(@Param("userId") Long userId,
                                @Param("skuIds") List<Long> skuIds);
}
