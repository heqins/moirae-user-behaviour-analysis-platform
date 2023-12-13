package com.admin.server.controller;

import com.admin.server.facade.AnalysisFacade;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import com.api.common.model.vo.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/report-data")
@Tag(name = "埋点上报数据入口")
public class ReportDataController {

    @Resource
    private AnalysisFacade analysisFacade;

    @Operation(description = "获取事件统计结果")
    @PostMapping("/eventCount")
    public CommonResponse<List<EventCountDto>> getEventCount(@RequestBody @Valid GetEventCountParam param) {
        return CommonResponse.ofSuccess(analysisFacade.getEventCount(param));
    }
}
