package com.java.community.mapper;

import com.java.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
    List<Question> selectHotQuestions();
    List<Question> selectByTag(String tag);

}