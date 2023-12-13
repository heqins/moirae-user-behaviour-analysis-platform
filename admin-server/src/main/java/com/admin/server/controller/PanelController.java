package com.admin.server.controller;

import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.panel.SaveReportTableParam;
import com.api.common.model.vo.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/panel")
@Tag(name = "报表入口")
public class PanelController {

    @Operation(description = "保存报表")
    @PostMapping("/saveReportTable")
    public CommonResponse<Void> saveReportTable(@RequestBody @Valid SaveReportTableParam param) {
        return CommonResponse.ofSuccess();
    }

//    @Operation(description = "获取所有可见报表")
//    @PostMapping("/tables")
//    public CommonResponse<Void> saveReportTable(@RequestBody @Valid SaveReportTableParam param) {
//        return CommonResponse.ofSuccess();
//    }
}
