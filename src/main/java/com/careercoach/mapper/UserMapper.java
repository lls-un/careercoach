package com.careercoach.mapper;

import com.careercoach.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    // 根据用户ID查询
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User selectByUserId(@Param("userId") String userId);

    // 新增用户
    @Insert("INSERT INTO user (user_id, name, email) VALUES (#{userId}, #{name}, #{email})")
    int insert(User user);
}
