package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.category.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 查询分类列表
     * @return
     */
    @Select("select * from category")
    List<Category> list();

    /**
     * 根据id查询分类
     * @param categoryId
     * @return
     */
    @Select("select * from category where id = #{categoryId}")
    Category selectById(Long categoryId);
}
