package com.java.community.enums;

/**
 * Author: yk
 * Date: 2020/5/17 17:13
 */
public enum NotificationTypeEnum {
    REPLY_QUESTION(1,"回复了我的话题"),
    REPLY_COMMENT(2,"回复了我的评论");

    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    NotificationTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String nameOfType(int type){
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType() == type){
                return notificationTypeEnum.getName();
            }
        }
        return "";
    }
}
