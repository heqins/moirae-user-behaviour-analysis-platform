package com.api.common.dto;

import lombok.Data;

/**
 * @author heqin
 */
@Data
public class TableFieldDTO {

    private String fieldName;

    private Class fieldType;

    private String tableName;

    private Integer lengthLimit;

    /**
     * 是否启用当前字段，0 - 否， 1 - 是
     */
    private Integer status;
}
