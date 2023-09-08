package com.admin.server.controller;

import com.admin.server.service.IAppService;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.CreateAppParam;
import com.api.common.model.vo.CommonResponse;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.AppPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/app")
@Tag(name = "应用管理")
public class AppController {
    @Resource
    private IAppService appService;

    @Operation(description = "创建应用")
    @PostMapping("")
    public CommonResponse<Void> createApp(@RequestBody @Valid CreateAppParam createAppParam) {
        appService.createApp(createAppParam);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "获取应用列表")
    @GetMapping("/available-app")
    public CommonResponse<PageVo<AppPageVo>> getAvailableApps(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return CommonResponse.ofSuccess(appService.getAvailableApps(pageNum, pageSize));
    }
}
