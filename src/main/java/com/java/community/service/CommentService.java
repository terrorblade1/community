package com.java.community.service;

import com.java.community.dto.CommentDTO;
import com.java.community.enums.CommentTypeEnum;
import com.java.community.model.Comment;
import com.java.community.model.User;

import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/9 20:24
 */
public interface CommentService {

    void save(Comment comment, User user);

    List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type);

}
