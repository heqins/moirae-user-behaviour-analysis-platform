package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "登录系统参数")
public class UserLoginParam {

    @Schema(description = "用户名", required = true)
    @NotBlank
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank
    private String password;

}
