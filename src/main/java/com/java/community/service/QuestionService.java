package com.java.community.service;

import com.java.community.dto.PaginationDTO;
import com.java.community.dto.QuestionDTO;
import com.java.community.model.Question;

import java.io.IOException;
import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/7 9:16
 */
public interface QuestionService {

    PaginationDTO findAll(Integer page, Integer size);

    PaginationDTO findByUserId(Long id, Integer page, Integer size);

    QuestionDTO findById(Long id);

    void saveOrModify(Question question);

    void incView(Long id);

    List<QuestionDTO> findRelated(QuestionDTO questionDTO);

    List<QuestionDTO> findHotQuestions();

    void saveDataToElasticSearch();

    PaginationDTO findByElasticSearch(String keyword, Integer page, Integer size) throws IOException;
}
