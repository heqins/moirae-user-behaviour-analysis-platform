package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "事件查询参数")
public class AnalysisAggregationParam {

    @Schema(description = "条件过滤")
    private AnalysisWhereFilterParam relation;

    @Schema(description = "事件名称")
    private String eventName;

    @Schema(description = "前台显示事件名称")
    private String eventNameForDisplay;

    @Schema(description = "查询类型", example = "default")
    private String type;

    @Schema(description = "")
    private List<String> selectAttributes;

}
