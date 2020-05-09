package com.java.community.mapper;

import com.java.community.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Author: yk
 * Date: 2020/5/6 14:36
 */
@Mapper
public interface UserMapper {

    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User selectByToken(@Param("token") String token);

    @Select("select * from user where id = #{id}")
    User selectById(@Param("id") Integer id);

    @Select("select * from user where account_id = #{accountId}")
    User selectByAccountId(@Param("accountId") String accountId);

    @Update("update user set name=#{name},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id=#{id}")
    void update(User user);

}
