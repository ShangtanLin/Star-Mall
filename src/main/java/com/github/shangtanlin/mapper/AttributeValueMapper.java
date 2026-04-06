package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.product.AttributeValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface AttributeValueMapper {

    @Select("select * from attribute_value where spu_id  = #{id}")
    List<AttributeValue> selectBySpuId(Long id);
}
