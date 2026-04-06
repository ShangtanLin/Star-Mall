package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.product.Attribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface AttributeMapper {
    @Select("select * from attribute where spu_id = #{id}")
    List<Attribute> selectBySpuId(Long id);
}
