package com.api.common.enums;

import java.util.Locale;
import java.util.Objects;

public enum AttributeDataTypeEnum {

    VARCHAR("VARCHAR", "java.lang.String", "字符串"),

    TINYINT("TINYINT", "java.lang.Byte", "小整型"),

    LARGEINT("LARGEINT", "java.lang.Long", "长整型"),

    DATE("DATE", "java.util.Date", "日期"),

    DECIMAL("DECIMAL", "java.math.BigDecimal", "科学计数"),

    INT("INT", "java.lang.Integer", "整型"),

    ;

    private String dorisType;

    private String javaType;

    private String description;

    AttributeDataTypeEnum(String dorisType, String javaType, String description) {
        this.dorisType = dorisType;
        this.javaType = javaType;
        this.description = description;
    }

    public static Boolean containType(String type) {
        String lowerCaseType = type.toLowerCase(Locale.ROOT);

        for (AttributeDataTypeEnum dataTypeEnum : AttributeDataTypeEnum.values()) {
            if (Objects.equals(dataTypeEnum.dorisType.toLowerCase(Locale.ROOT), lowerCaseType)) {
                return true;
            }
        }

        return false;
    }

    public static String generateDorisTypeWithLength(String type, Integer length, Integer limit) {
        if (!containType(type)) {
            return null;
        }

        switch (type) {
            case "VARCHAR":
                return "VARCHAR(" + length + ")";
            case "DECIMAL":
                return "DECIMAL(" + length + "," + limit + ")";
            default:
                break;
        }

        return type;
    }
}
