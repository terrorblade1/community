package com.java.community.dto;

import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/9 20:19
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
