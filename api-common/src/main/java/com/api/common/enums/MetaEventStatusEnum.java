package com.api.common.enums;

import java.util.Objects;

public enum MetaEventStatusEnum {
    ENABLE(1, "启用"),

    DISABLE(0, "禁用");

    private Integer status;

    private String description;

    MetaEventStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public static Boolean isEnable(Integer status) {
        return Objects.equals(status, ENABLE.status);
    }
}
