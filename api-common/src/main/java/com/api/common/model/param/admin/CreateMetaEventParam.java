package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "创建元事件参数")
public class CreateMetaEventParam {

    @Schema(description = "应用id", required = true)
    @NotBlank(message = "应用id不能为空")
    private String appId;

    @Schema(description = "事件名称", required = true)
    @NotBlank(message = "事件名称不能为空")
    private String eventName;

    @Schema(description = "显示名称")
    private String showName;

}
