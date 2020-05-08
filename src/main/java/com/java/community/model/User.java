package com.java.community.model;

import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/6 14:40
 */
@Data
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;
}
