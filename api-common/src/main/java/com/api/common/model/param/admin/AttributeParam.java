package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "属性参数")
public class AttributeParam {

    @Schema(description = "属性名称", required = true)
    @NotBlank
    private String attributeName;

    @Schema(description = "数据类型", required = true)
    @NotBlank
    private String dataType;

    private Integer length;

    private Integer limit;

    @Schema(description = "显示名称")
    private String showName;

}
