package com.admin.server.controller;

import com.admin.server.service.IUserService;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.UserLoginParam;
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
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/user")
@Tag(name = "登录接口")
public class UserController {

    @Resource
    private IUserService userService;

    @Operation(description = "登录系统")
    @PostMapping("/doLogin")
    public CommonResponse<Void> doLogin(@RequestBody @Valid UserLoginParam userLoginParam) {
        userService.doLogin(userLoginParam);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "登出系统")
    @GetMapping("/doLogout")
    public CommonResponse<Void> doLogout() {
        userService.doLogout();
        return CommonResponse.ofSuccess();
    }
}
