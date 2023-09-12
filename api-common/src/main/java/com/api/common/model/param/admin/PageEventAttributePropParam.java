package com.api.common.model.param.admin;

import com.api.common.model.param.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "分页查询事件属性参数")
public class PageEventAttributePropParam extends PageParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "事件名称", required = true)
    @NotBlank
    private String eventName;

}
