package com.api.common.param.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel(description = "创建元事件属性参数")
public class CreateMetaEventAttributeParam {

    @ApiParam(value = "应用id", required = true)
    @NotBlank
    private String appId;

    @ApiParam(value = "事件名称")
    @NotBlank
    private String eventName;

    @ApiParam(value = "显示名称")
    private String showName;

    private List<AttributeParam> attributes;

}
