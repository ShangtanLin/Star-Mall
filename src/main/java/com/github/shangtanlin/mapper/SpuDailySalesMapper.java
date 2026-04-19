package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.product.SpuDailySales;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface SpuDailySalesMapper extends BaseMapper<SpuDailySales> {

    /**
     * 查询昨日热门TOP10的spuId列表（按销量降序）
     */
    @Select("SELECT spu_id FROM spu_daily_sales ORDER BY sales DESC LIMIT 10")
    List<Long> selectTop10SpuIds();

    /**
     * 查询昨日热门TOP10（包含销量，按销量降序）
     */
    @Select("SELECT id, spu_id, sales, sale_date, create_time FROM spu_daily_sales ORDER BY sales DESC LIMIT 10")
    List<SpuDailySales> selectTop10WithSales();

    /**
     * 清空表
     */
    @Delete("DELETE FROM spu_daily_sales")
    void deleteAll();

    /**
     * 批量插入每日销量数据
     */
    @Insert("""
        <script>
        INSERT INTO spu_daily_sales (id, spu_id, sales, sale_date, create_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.id}, #{item.spuId}, #{item.sales}, #{item.saleDate}, #{item.createTime})
        </foreach>
        </script>
    """)
    void batchInsert(@Param("list") List<SpuDailySales> list);
}