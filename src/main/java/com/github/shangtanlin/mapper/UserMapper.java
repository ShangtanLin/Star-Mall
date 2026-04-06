package com.github.shangtanlin.mapper;

import com.github.shangtanlin.model.entity.user.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into  user (phone) values (#{phone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Select("select * from user where phone = #{phoneNumber}")
    User selectByPhone(String phoneNumber);

    @Select("select * from user where id = #{id}")
    User selecyById(Long id);
}
