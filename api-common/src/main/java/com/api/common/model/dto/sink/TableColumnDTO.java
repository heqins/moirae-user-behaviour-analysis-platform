package com.api.common.model.dto.sink;

import lombok.Data;

import java.util.Objects;

/**
 * @author heqin
 */
@Data
public class TableColumnDTO {

    private String columnName;

    private String columnType;

    private String tableName;

    private Integer lengthLimit;

    private Boolean nullable;

    /**
     * 是否启用当前字段，0 - 否， 1 - 是
     */
    private Integer status;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableColumnDTO that = (TableColumnDTO) o;
        return columnName.equals(that.columnName) && tableName.equals(that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, tableName);
    }
}
