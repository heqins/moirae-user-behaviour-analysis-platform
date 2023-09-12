package com.admin.server.error;

public enum ErrorCodeEnum {

    META_EVENT_EXIST(1001, "元事件已存在"),
    META_EVENT_NOT_EXIST(1002, "元事件不存在"),

    APP_NOT_EXIST(2001, "App不存在"),

    META_EVENT_ATTRIBUTE_NOT_EXIST(3001, "元事件属性不存在"),
    META_EVENT_ATTRIBUTE_UPDATE_FORBID(3002, "元事件属性更改异常，请重新检查"),

    META_EVENT_ATTRIBUTE_CREATE_PARAM_ERROR(3003, "元事件属性创建参数异常"),

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
