package com.admin.server.error;

public enum ErrorCodeEnum {

    META_EVENT_EXIST(1001, "元事件已存在"),
    META_EVENT_NOT_EXIST(1002, "元事件不存在"),

    ;

    private Integer code;

    private String msg;


    ErrorCodeEnum(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
