package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "事件查询参数")
public class AnalysisParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "窗口类型", example = "按天")
    private String windowFormat;

    private List<String> dateRange;

    private List<String> groupBy;

    private AnalysisWhereFilterParam whereFilter;

    private List<AnalysisAggregationParam> aggregations;

    public boolean isValid() {
        if (CollectionUtils.isEmpty(aggregations)) {
            return false;
        }

        return true;
    }
}
