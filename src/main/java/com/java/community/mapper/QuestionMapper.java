package com.java.community.mapper;

import com.java.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/6 20:41
 */
@Mapper
public interface QuestionMapper {

    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void insert(Question question);

    @Select("select * from question limit #{offset},#{size}")
    List<Question> selectAll(@Param(value = "offset") Integer offset,@Param(value = "size") Integer size);

    @Select("select * from question where creator=#{id} limit #{offset},#{size}")
    List<Question> selectById(@Param(value = "id") Integer id,@Param(value = "offset") Integer offset,@Param(value = "size") Integer size);

    @Select("select count(1) from question")
    Integer selectCount();

    @Select("select count(1) from question where creator=${id}")
    Integer selectCountById(@Param(value = "id") Integer id);
}