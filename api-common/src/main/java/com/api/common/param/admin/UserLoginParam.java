package com.api.common.param.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "登录系统参数")
public class UserLoginParam {

    @ApiParam(value = "用户名", required = true)
    @NotBlank
    private String username;

    @ApiParam(value = "密码", required = true)
    @NotBlank
    private String password;

}
