package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.order.SubOrder;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 子订单mapper
 */
@Mapper
public interface SubOrderMapper extends BaseMapper<SubOrder> {

    /**
     * 根据主订单Id和用户Id修改子订单状态
     * @param orderSn
     * @param userId
     * @param status
     */
    @Update("update sub_order set status = #{status} " +
            "where parent_order_sn = #{orderSn} and user_id = #{userId}")
    void setStatusByParentSn(
            @Param("orderSn") String orderSn,
            @Param("userId") Long userId,
            @Param("status") Integer status);


    /**
     * 根据父订单编号查询子订单
     * @param parentOrderSn
     * @return
     */
    @Select("select * from sub_order where parent_order_sn = #{parentOrderSn}")
    SubOrder selectByParentId(@Param("parent_order_sn") String parentOrderSn);


    /**
     * 修改子订单状态
     * @param parentOrderSn(主订单编号)
     * @param status
     */
    @Update("update sub_order set status = #{status} " +
            "where parent_order_sn = #{parentOrderSn}")
    void setStatusWithoutUserId(
            @Param("parentOrderSn") String parentOrderSn,
            @Param("status") Integer status);



    /**
     * 修改子订单支付时间和支付类型
     * @param parentOrderSn
     * @param paymentType
     * @param paymentTime
     */
    @Update("update sub_order set payment_type = #{paymentType}, " +
            "payment_time = #{paymentTime} " +
            "where parent_order_sn = #{parentOrderSn}")
    void setPaymentType(
            @Param("parentOrderSn") String parentOrderSn,
            @Param("paymentType") Integer paymentType,
            @Param("paymentTime") LocalDateTime paymentTime);


    /**
     * 修改子订单状态
     * @param subOrderSn
     * @param status
     * @return
     */
    @Update("update sub_order set status = #{status} " +
            "where sub_order_sn = #{subOrderSn}")
    int updateStatus(
            @Param("subOrderSn") String subOrderSn,
            @Param("status") Integer status);


    /**
     * 查询未成功的订单数量
     * @param parentOrderSn
     * @return
     */
    @Select("select count(*) from sub_order where " +
            "parent_order_sn = #{parentOrderSn} " +
            "and status in (1,2,5) ")
    int selectUnSuccess(@Param("parentOrderSn") String parentOrderSn);


    /**
     * 根据主订单编号查询所有子订单ID
     * @param parentOrderSn
     * @return
     */
    @Select("select id from sub_order " +
            "where parent_order_sn = #{parentOrderSn}")
    List<Long> selectIdsByParentSn(@Param("parentOrderSn") String parentOrderSn);


    /**
     * 根据子订单编号查询子订单
     * @param subOrderSn
     */
    @Select("select * from sub_order " +
            "where sub_order_sn = #{subOrderSn}")
    SubOrder selectByOrderSn(@Param("subOrderSn") String subOrderSn);


    /**
     * 根据主订单编号查询子订单编号
     * @param parentOrderSn
     * @return
     */
    @Select("select sub_order_sn from sub_order " +
            "where parent_order_sn = #{parentOrderSn}")
    List<String> selectSnByParentSn(@Param("parentOrderSn") String parentOrderSn);


    /**
     * 根据子订单编号修改子订单状态
     * @param subOrderSn
     * @param status
     */
    @Update("update sub_order set status = #{status} " +
            "where sub_order_sn = #{subOrderSn}")
    void setStatusBySubOrderSn
            (@Param("subOrderSn") String subOrderSn,
             @Param("status") Integer status);


    ///**
    // * 插入子订单
    // * @param subOrder
    // */
    //@Insert("insert into `taobao-mall`.order_sub " +
    //        "(id, parent_order_id, parent_order_sn," +
    //        " sub_order_sn, shop_id, delivery_company," +
    //        " delivery_sn, receiver_name, receiver_phone, " +
    //        "receiver_province, receiver_city, receiver_district, " +
    //        "receiver_detail_address, payment_time,is_delete) values " +
    //        "(#{id},#{parentOrderId},#{parentOrderSn},#{subOrderSn}," +
    //        "#{shopId},#{deliveryCompany},#{deliverySn},#{receiverName}," +
    //        "#{receiverPhone},#{receiverProvince},#{receiverCity},#{receiverDistrict}," +
    //        "#{receiverDetailAddress},#{paymentTime},#{isDelete})")
    //void insert(SubOrder subOrder);


    ///**
    // * 根据主订单id查询子订单
    // * @param orderId
    // * @return
    // */
    //@Select("select * from `taobao-mall`.order_sub " +
    //        "where parent_order_id = #{orderId}")
    //SubOrder selectById(@Param("orderId") Long orderId);




}
