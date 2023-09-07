package com.api.common.param.admin;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AttributeParam {

    @ApiParam(value = "属性名称", required = true)
    @NotBlank
    private String attributeName;

    @ApiParam(value = "数据类型", required = true)
    @NotBlank
    private String dataType;

    private Integer length;

    private Integer limit;

    @ApiParam(value = "显示名称")
    private String showName;

}
