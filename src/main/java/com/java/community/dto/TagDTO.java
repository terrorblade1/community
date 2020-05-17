package com.java.community.dto;

import lombok.Data;

import java.util.List;

/**
 * Author: yk
 * Date: 2020/5/17 10:08
 */
@Data
public class TagDTO {
    private String categoryName;  //类别
    private List<String> tags;

}
