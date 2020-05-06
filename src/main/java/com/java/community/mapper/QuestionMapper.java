package com.java.community.mapper;

import com.java.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: yk
 * Date: 2020/5/6 20:41
 */
@Mapper
public interface QuestionMapper {

    @Insert("insert into (title,description,gmt_create,gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);

}
