package com.java.community.dto;

import com.java.community.model.User;
import lombok.Data;

/**
 * Author: yk
 * Date: 2020/5/17 18:45
 */
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;
    private Long outerid;
    private String typeName;
    private Integer type;
}
