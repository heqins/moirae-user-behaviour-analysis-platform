package com.api.common.model.param.admin.reportData;

import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "获取事件上报数量参数")
public class GetEventCountParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "分析时间范围 第一个为起始，第二个为结束")
    private List<String> dateRange;

}
