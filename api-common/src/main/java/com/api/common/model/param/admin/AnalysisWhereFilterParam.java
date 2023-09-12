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

        private List<String> values;
    }
}
