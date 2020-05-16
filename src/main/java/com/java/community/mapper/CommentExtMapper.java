package com.java.community.mapper;

import com.java.community.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment record);
}