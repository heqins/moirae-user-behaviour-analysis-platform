package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "创建应用参数")
public class CreateAppParam {

    @Schema(description = "应用名称", required = true)
    @NotBlank
    private String appName;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "保存月数")
    private Integer saveMonth;

}
