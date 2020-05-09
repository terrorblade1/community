package com.java.community.exception;

/**
 * Author: yk
 * Date: 2020/5/9 16:17
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUNT("此话题不存在！");

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomizeErrorCode(String message) {
        this.message = message;
    }

}
