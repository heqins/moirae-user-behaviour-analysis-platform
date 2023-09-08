package com.admin.server.controller;

import com.admin.server.service.IAppService;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.CreateAppParam;
import com.api.common.model.vo.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/role")
@Tag(name = "角色管理")
public class RoleController {

    @Resource
    private IAppService appService;

    @Operation(description = "创建角色")
    @PostMapping("")
    public CommonResponse<Void> createApp(@RequestBody @Valid CreateAppParam createAppParam) {
        appService.createApp(createAppParam);
        return CommonResponse.ofSuccess();
    }

}
