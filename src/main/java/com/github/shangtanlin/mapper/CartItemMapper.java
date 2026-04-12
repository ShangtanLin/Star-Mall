package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.cart.CartItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 查询用户购物车记录
     * @param userId
     * @return
     */
    @Select("select * from cart_item where user_id = #{userId} and is_delete = 0")
    List<CartItem> list(@Param("userId")Long userId);

    //@Update("update cart_item set " +
    //        "quantity = #{quantity}" +
    //        "checked = #{checked}" +
    //        "where sku_id = #{skuId}")
    //void updateBySkuId(Long skuId, CartRedisJson cartRedisJson);

    @Delete("delete from cart_item where user_id = #{userId} and sku_id = #{skuId}")
    int delete(@Param("userId") Long userId, @Param("skuId") Long skuId);


    /**
     * 清除购物车（逻辑删除）
     * @param userId
     * @return
     */
    @Update("UPDATE cart_item SET is_delete = 1, update_time = #{msgTime} " +
            "WHERE user_id = #{userId} AND is_deleted = 0 AND update_time < #{msgTime}")
    int cleanAll(@Param("userId") Long userId, @Param("msgTime") LocalDateTime msgTime);




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


    /**
     * 根据skuId和userId查询单条记录
     * @param userId
     * @param skuId
     * @return
     */
    @Select("select * from cart_item where user_id = #{userId} " +
            "and sku_id = #{skuId}")
    CartItem selectOne(
            @Param("userId") Long userId,
            @Param("skuId") Long skuId);
}
