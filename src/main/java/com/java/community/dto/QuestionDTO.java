package com.java.community.dto;

import com.java.community.model.User;
import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/7 14:55
 */
@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private User user;
}
