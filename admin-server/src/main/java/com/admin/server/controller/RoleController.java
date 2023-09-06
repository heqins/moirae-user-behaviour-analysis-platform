package com.admin.server.controller;

import com.admin.server.service.IAppService;
import com.api.common.constant.ApiConstants;
import com.api.common.param.admin.CreateAppParam;
import com.api.common.vo.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/role")
@Api(tags = "角色管理")
public class RoleController {

    @Resource
    private IAppService appService;

    @ApiOperation(value = "创建角色")
    @PostMapping("")
    public CommonResponse<Void> createApp(@RequestBody CreateAppParam createAppParam) {
        appService.createApp(createAppParam);
        return CommonResponse.ofSuccess();
    }

}
