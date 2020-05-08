package com.java.community.model;

import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/6 20:48
 */
@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
}