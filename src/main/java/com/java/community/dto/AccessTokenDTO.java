package com.java.community.dto;

import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/6 8:55
 * access_token
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
