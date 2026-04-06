package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.product.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    @Select("select * from sku where spu_id = #{spuId}")
    List<Sku> selectBySpuId(Long spuId);

    @Select("select * from sku where id = #{id}")
    Sku selectById(Long id);

    /**
     * 扣减库存
     *
     * @param skuId
     * @param quantity
     * @return
     */
    //-- 这种写法能彻底避免“超卖”，且不需要加繁琐的分布式锁, 原子操作 + 临界值检查
    @Update("update `taobao-mall`.sku set stock = stock - #{quantity} " +
            "where id = #{skuId} and stock >= #{quantity}")
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);



    /**
     * 回补库存
     * @param skuId
     * @param quantity
     */
    @Update("update `taobao-mall`.sku set stock = stock + #{quantity} " +
            "where id = #{skuId}")
    void rollbackStock(@Param("skuId") Long skuId,@Param("quantity") Integer quantity);


    /**
     * 增加库存（）回补库存
     * @param skuId
     * @param quantity
     * @return
     */
    @Update("update sku set stock = stock + #{quantity} " +
            "where id = #{skuId}")
    int addStock(@Param("skuId") Long skuId,@Param("quantity") Integer quantity);
}
