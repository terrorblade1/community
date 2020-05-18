package com.java.community.enums;

/**
 * Author: yk
 * Date: 2020/5/17 17:30
 */
public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1);

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
