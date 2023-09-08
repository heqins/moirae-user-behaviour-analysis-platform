package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "创建元事件属性参数")
public class CreateMetaEventAttributeParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "事件名称")
    @NotBlank
    private String eventName;

    private List<AttributeParam> attributes;

}
