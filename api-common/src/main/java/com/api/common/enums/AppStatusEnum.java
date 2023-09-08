package com.api.common.enums;

public enum AppStatusEnum {

    ENABLE(1, "启用"),

    DISABLE(0, "关闭"),

    ;

    private Integer status;

    private String description;

    AppStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
