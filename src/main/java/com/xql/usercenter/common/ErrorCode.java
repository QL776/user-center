package com.xql.usercenter.common;

public enum ErrorCode {

    SUCCESS(0, "Success", ""),
    PARAMS_ERROR(4000, "Bad Request", ""),
    NULL_ERROR(4001, "Internal Server Error", ""),
    NO_LOGIN(40100, "Unauthorized", ""),
    NO_AUTH(40101, "Forbidden", ""),
    SYSTEM_ERROR(50000, "Internal Server Error", "");

    private final int code;        // 状态码
    private final String message;  // 状态码信息
    private final String description; // 描述

    // 构造函数
    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    // 获取状态码
    public int getCode() {
        return code;
    }

    // 获取状态码信息
    public String getMessage() {
        return message;
    }

    // 获取描述
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
