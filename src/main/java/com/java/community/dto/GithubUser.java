package com.java.community.dto;

import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/6 9:41
 * 用户信息
 */
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;
}
