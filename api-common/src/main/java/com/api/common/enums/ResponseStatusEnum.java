package com.api.common.enums;

public enum ResponseStatusEnum {

    SUCCESS(200, "成功"),

    UNAUTHORIZED(401, "认证失败"),

    FORBIDDEN(403, "禁止访问"),

    UNKNOWN_ERROR(500, "未知错误"),

    ;

    private String msg;

    private Integer code;

    ResponseStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
