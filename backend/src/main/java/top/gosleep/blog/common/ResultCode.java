package com.blog.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    // 2xx: 成功
    SUCCESS(200, "成功"),

    // 4xx：客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足，无法操作"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),  // 如：用户名已存在、版本冲突
    UNPROCESSABLE_ENTITY(422, "业务逻辑错误"),

    // 5xx：服务端业务逻辑错误
    BUSINESS_ERROR(501, "业务处理失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
