package com.admin.server.controller;

import com.admin.server.facade.MetaFacade;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.PageEventAttributePropParam;
import com.api.common.model.vo.CommonResponse;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.EventAttributePropPageVo;
import com.api.common.model.vo.admin.MetaEventsPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private MetaFacade metaFacade;

    @Operation(description = "进行事件查询")
    @PostMapping("/doEventAnalysis")
    public CommonResponse<PageVo<EventAttributePropPageVo>> doEventAnalysis(@RequestBody @Valid PageEventAttributePropParam param) {
        return CommonResponse.ofSuccess();
    }


}
