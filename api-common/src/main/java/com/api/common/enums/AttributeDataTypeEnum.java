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

    public String getDorisType() {
        return this.dorisType;
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

    public static String getDataTypeDescription(String type) {
        for (AttributeDataTypeEnum dataTypeEnum : AttributeDataTypeEnum.values()) {
            if (dataTypeEnum.dorisType.startsWith(type)) {
                return dataTypeEnum.description;
            }
        }

        return "";
    }

    public static String getDefaultDataTypeByClass(String className) {
        // todo: boolean,
        String type = null;
        switch (className) {
            case "java.lang.String":
                type = "VARCHAR(64)";
                break;
            case "java.lang.Integer":
                type = "INT";
                break;
            case "java.lang.Long":
                type = "LARGEINT";
                break;
            case "java.lang.Byte":
                type = "TINYINT";
                break;
            case "java.util.Date":
                type = "DATE";
                break;
            case "java.math.BigDecimal":
                type = "DECIMAL(10,2)";
                break;
            default:
                break;
        }

        return type;
    }

    public static String generateDorisTypeWithLength(String type, Integer length, Integer limit) {
        if (!containType(type)) {
            return null;
        }

        switch (type) {
            case "VARCHAR":
                if (length == null) {
                    return null;
                }

                return "VARCHAR(" + length + ")";
            case "DECIMAL":
                if (length == null || limit == null) {
                    return null;
                }

                return "DECIMAL(" + length + "," + limit + ")";
            default:
                break;
        }

        return type;
    }
}
