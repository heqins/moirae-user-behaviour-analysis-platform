package com.admin.server.controller;

import com.admin.server.service.IAppService;
import com.admin.server.service.IUserService;
import com.api.common.constant.ApiConstants;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.CreateAppParam;
import com.api.common.param.admin.UserLoginParam;
import com.api.common.vo.CommonResponse;
import com.api.common.vo.admin.AppPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/user")
@Api(tags = "登录接口")
public class UserController {

    @Resource
    private IUserService userService;

    @ApiOperation(value = "登录系统")
    @PostMapping("/doLogin")
    public CommonResponse<Void> doLogin(@RequestBody UserLoginParam userLoginParam) {
        userService.doLogin(userLoginParam);
        return CommonResponse.ofSuccess();
    }

    @ApiOperation(value = "登出系统")
    @GetMapping("/doLogout")
    public CommonResponse<Void> doLogout() {

        return CommonResponse.ofSuccess();
    }
}
