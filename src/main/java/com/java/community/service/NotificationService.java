package com.java.community.service;

import com.java.community.dto.NotificationDTO;
import com.java.community.dto.PaginationDTO;
import com.java.community.model.User;

/**
 * Author: yk
 * Date: 2020/5/17 18:43
 */
public interface NotificationService {
    PaginationDTO findById(Long id, Integer page, Integer size);

    Long unreadCount(Long id);

    NotificationDTO read(Long id, User user);
}
