package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "事件查询参数")
public class AnalysisParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    private String windowFormat;

    private List<String> dateRange;

    private List<String> groupBy;

    private AnalysisWhereFilterParam whereFilter;

    private List<AnalysisAggregationParam> aggregations;

}
