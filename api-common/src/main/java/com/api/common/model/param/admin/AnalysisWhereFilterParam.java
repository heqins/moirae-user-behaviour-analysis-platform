package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "事件查询参数")
public class AnalysisWhereFilterParam {

    private String relation;

    private List<Filter> filters;

    public static class Filter {
        private String columnName;

        private String comparator;

        private String value;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getComparator() {
            return comparator;
        }

        public void setComparator(String comparator) {
            this.comparator = comparator;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isValid() {
            if (columnName == null || comparator == null || value == null) {
                return false;
            }

            return true;
        }
    }
}
