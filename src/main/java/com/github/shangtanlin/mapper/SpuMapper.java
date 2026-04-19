package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.product.Spu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SpuMapper extends BaseMapper<Spu> {

    @Select("select * from spu where id = #{id}")
    Spu selectById(Long id);

    @Select("select * from spu")
    List<Spu> selectList();

    @Select("select id,name,description,main_image,sales,min_price from spu " +
            "where match (search_keywords) against (#{keyword} in natural language mode) " +
            "order by sales desc")
    List<Spu> selectListByKeyWord(String keyword);


    @Select("""
    <script>
        SELECT *
        FROM spu
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </script>
    """)
    List<Spu> selectHotByIds(@Param("ids") List<Long> ids);

    /**
     * 增加商品销量
     * @param spuId 商品ID
     * @param quantity 增加的数量
     */
    @Update("UPDATE spu SET sales = sales + #{quantity} WHERE id = #{spuId}")
    void incrementSales(@Param("spuId") Long spuId, @Param("quantity") Integer quantity);

}
