package com.java.community.dto;

import com.java.community.model.User;
import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/10 20:39
 */
@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    //private Integer commentCount;
    private String content;
    private User user;
}
