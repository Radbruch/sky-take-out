package com.sky.mapper;

import com.sky.annocation.AutoFill;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getUserByOpenId(String openid);


    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getUserByUserId(Long userId);

}
