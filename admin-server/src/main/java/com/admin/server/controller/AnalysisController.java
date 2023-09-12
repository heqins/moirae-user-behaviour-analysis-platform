package com.admin.server.controller;

import com.admin.server.facade.AnalysisFacade;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.vo.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/analysis")
@Tag(name = "分析入口")
public class AnalysisController {

    @Resource
    private AnalysisFacade analysisFacade;

    @Operation(description = "进行事件查询")
    @PostMapping("/doEventAnalysis")
    public CommonResponse<Void> doEventAnalysis(@RequestBody @Valid AnalysisParam param) {
        analysisFacade.doEventAnalysis(param);
        return CommonResponse.ofSuccess();
    }


}
