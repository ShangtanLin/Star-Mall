package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.order.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据主订单编号查询订单项
     * @param orderSn
     * @return
     */
    @Select("select * from order_item " +
            "where parent_order_sn = #{orderSn}")
    List<OrderItem> selectByParentSn(@Param("orderSn") String orderSn);





    ///**
    // * 插入订单项目
    // * @param orderItem
    // */
    //@Insert("insert into `taobao-mall`.order_item " +
    //        "(sub_order_id, spu_id, sku_id, spu_name," +
    //        " sku_name, price, quantity) " +
    //        "values (#{orderId},#{spuId},#{skuId},#{spuName}," +
    //        "#{skuName},#{price},#{quantity})")
    //void insertOrderItem(OrderItem orderItem);
    //
    ///**
    // * 查询订单项目表
    // * @param orderId
    // * @return
    // */
    //@Select("select * from `taobao-mall`.order_items where sub_order_id = #{orderId}")
    //List<OrderItem> getByOrderId(Long orderId);
}
