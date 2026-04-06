package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.order.ParentOrder;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入主订单
     * @param parentOrder
     */
    @Insert("insert into `taobao-mall`.`order` " +
            "(id, order_sn, user_id, pay_amount,total_amount,status," +
            "payment_type,payment_time," +
            "coupon_user_record_id,coupon_amount) " +
            "VALUES (#{id},#{orderSn},#{userId},#{payAmount}," +
            "#{totalAmount},#{status},#{paymentType},#{paymentTime}" +
             "#{couponUserRecordId},#{couponAmount})")
    void insert(ParentOrder parentOrder);

    /**
     * 根据订单id和用户id询主订单
     * @param orderId
     * @param userId
     * @return
     */
    @Select("select * from `taobao-mall`.`order` where id = #{orderId} and user_id = #{userId}")
    ParentOrder selectById(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /**
     * 根据订单编号和用户id查询主订单
     * @param orderSn
     * @param userId
     * @return
     */
    @Select("select * from `taobao-mall`.`order` where order_sn = #{orderSn} and user_id = #{userId}")
    ParentOrder selectByOrderSn(@Param("orderSn") String orderSn, @Param("userId") Long userId);


    /**
     * 根据订单编号和用户id修改主订单状态
     * @param orderSn
     * @param userId
     * @param status
     */
    @Update("update `taobao-mall`.`order` set status = #{status} " +
            "where order_sn = #{orderSn} and user_id = #{userId}")
    void setStatus(@Param("orderSn")String orderSn, @Param("userId") Long userId,
                   @Param("status") Integer status);



    ///**查询主订单项目
    // * @param orderId
    // * @return
    // */
    //@Select("select * from `taobao-mall`.`order_item` where order_id = #{orderId}")
    //List<OrderItem> getOrderItems(Long orderId);


    /**
     * 根据订单编号查询主订单详情
     * @param orderSn
     */
    @Select("select * from `taobao-mall`.`order` where order_sn = #{orderSn}")
    ParentOrder getByOrderSn(String orderSn);


    /**
     * 根据用户id和订单状态查询主订单列表 （使用pageHelper）
     * @return
     */
    List<ParentOrder> selectOrderList(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据订单编号和订单状态设置主订单收货时间
     * @param receiveTime
     */
    @Update("update `taobao-mall`.`order` set receive_time = #{receiveTime} " +
            "where order_sn = #{orderSn} and user_id = #{userId}")
    void setReceiveTime(@Param("orderSn")String orderSn, @Param("userId") Long userId,
                        @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 根据订单编号和用户id逻辑删除订单
     * @param orderSn
     * @param userId
     */
    @Update("update `taobao-mall`.`order` set is_delete = 1 " +
            "where order_sn = #{orderSn} and user_id = #{userId}")
    void deleteById(@Param("orderSn") String orderSn, @Param("userId") Long userId);


    /**
     * 根据订单编号修改订单状态
     * @param orderSn
     * @param status
     */
    @Update("update `taobao-mall`.`order` set status = #{status} " +
            "where order_sn = #{orderSn}")
    void setStatusWithoutUserId(@Param("orderSn")String orderSn,
                   @Param("status") Integer status);


    /**
     * 根据订单编号查询订单详情
     * @param orderSn
     * @return
     */
    @Select("select * from `taobao-mall`.`order` where order_sn = #{orderSn}")
    ParentOrder selectByOrderSnWithoutUserId(String orderSn);


    /**
     * 根据订单编号修改支付类型
     * @param orderSn
     * @param paymentType
     */
    @Update("update `taobao-mall`.`order` set payment_type = #{paymentType} " +
            "where order_sn = #{orderSn}")
    void setPaymentType(@Param("orderSn") String orderSn, @Param("paymentType") Integer paymentType);

    /**
     * 根据订单编号修改支付时间
     * @param orderSn
     * @param paymentTime
     */
    @Update("update `taobao-mall`.`order` set payment_time = #{paymentTime} " +
            "where order_sn = #{orderSn}")
    void setPaymentTime(@Param("orderSn") String orderSn, @Param("paymentTime") LocalDateTime paymentTime);
}
