package com.api.common.enums;

public enum TableFieldEnum {

    STR("String"),

    INT("Integer"),


    ;
    private String typeName;

    TableFieldEnum(String typeName) {
        this.typeName = typeName;
    }
}
