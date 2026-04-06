package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BrandMapper {
    @Select("select * from brand where id = #{id}")
    Brand selectById(Long id);
}
