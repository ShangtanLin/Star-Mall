package com.github.shangtanlin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.shangtanlin.model.entity.shop.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
    //@Select("select * from shop where id = #{shopId}")
    //Shop selectById(Long shopId);
}
