package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "事件查询参数")
public class AnalysisAggregationParam {

    private AnalysisWhereFilterParam relation;

    private String eventName;

    private String eventNameForDisplay;

    private String type;

    private List<String> selectAttributes;

}
