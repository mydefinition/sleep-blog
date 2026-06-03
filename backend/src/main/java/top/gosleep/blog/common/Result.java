package top.gosleep.blog.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    private Result(ResultCode code, T data) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.data = data;
    }

    private Result(ResultCode code, String message, T data) {
        this.code = code.getCode();
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> error(ResultCode code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResultCode code) {
        return new Result<>(code, null);
    }

    /** 默认使用 BUSINESS_ERROR 错误码 */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.BUSINESS_ERROR, message, null);
    }
}
