package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "分析参数")
public class AnalysisParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "窗口类型", example = "按天")
    private String windowFormat;

    @Schema(description = "窗口时间", example = "2")
    private Integer windowTime;

    @Schema(description = "分析时间范围 第一个为起始，第二个为结束")
    private List<String> dateRange;

    @Schema(description = "分组字段")
    private List<String> groupBy;

    @Schema(description = "属性过滤条件")
    private AnalysisWhereFilterParam whereFilter;

    @Schema(description = "分析聚合维度")
    private List<AnalysisAggregationParam> aggregations;

}
