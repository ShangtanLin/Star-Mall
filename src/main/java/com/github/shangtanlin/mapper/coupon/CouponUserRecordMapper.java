package com.github.shangtanlin.mapper.coupon;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.coupon.CouponUserRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponUserRecordMapper extends BaseMapper<CouponUserRecord> {

    /**
     * 安全更新优惠券状态（带原状态校验，防止并发冲突）
     * * @param id           优惠券记录唯一ID
     * @param targetStatus 目标状态（如：1-已锁定，2-已使用，0-未使用）
     * @param originStatus 期望的当前状态（只有匹配此状态才会更新成功）
     * @return 影响行数：1-更新成功，0-状态不匹配或记录不存在
     */
    @Update("UPDATE coupon_user_record " +
            "SET status = #{targetStatus} " +
            "WHERE id = #{id} " +
            "AND status = #{originStatus}")
    int updateStatus(@Param("id") Long id,
                     @Param("targetStatus") Integer targetStatus,
                     @Param("originStatus") Integer originStatus);
}
