package com.java.community.service;

import com.java.community.dto.PaginationDTO;

/**
 * Author: yk
 * Date: 2020/5/7 9:16
 */
public interface QuestionService {

    void save(String title, String description, String tag, Integer creator);

    PaginationDTO findAll(Integer page, Integer size);

    PaginationDTO findById(Integer id, Integer page, Integer size);
}
