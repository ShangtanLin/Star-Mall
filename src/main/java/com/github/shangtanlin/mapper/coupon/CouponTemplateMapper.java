package com.github.shangtanlin.mapper.coupon;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.coupon.CouponTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


/**
 * 优惠券模板 Mapper 接口
 * 继承 BaseMapper 后，你就拥有了 CRUD（增删改查）的所有能力
 */
@Mapper
public interface CouponTemplateMapper extends BaseMapper<CouponTemplate> {

    /**
     * 原子扣减库存（增加已领取数量）
     * @param id 模板ID
     * @return 影响行数。返回 1 表示扣减成功，返回 0 表示库存不足
     */
    @Update("UPDATE `taobao-mall`.coupon_template " +
            "SET received_count = received_count + 1 " +
            "WHERE id = #{id} " +
            "AND received_count < publish_count")
    int updateReceivedCount(@Param("id") Long id);


}
