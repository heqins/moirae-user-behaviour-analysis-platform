package com.api.common.param.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "创建应用参数")
public class CreateAppParam {

    @ApiParam(value = "应用名称", required = true)
    @NotBlank
    private String appName;

    @ApiParam(value = "应用描述")
    private String description;

    @ApiParam(value = "保存月数")
    private Integer saveMonth;

}
