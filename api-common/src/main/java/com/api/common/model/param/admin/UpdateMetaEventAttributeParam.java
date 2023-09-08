package com.api.common.model.param.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author heqin
 */
@Data
@Schema(description = "更改元事件属性参数")
public class UpdateMetaEventAttributeParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;

    @Schema(description = "事件名称", required = true)
    @NotBlank
    private String eventName;

    private String attributeName;

    private Integer status;

    private String dataType;

    private Integer length;

    private Integer limit;

    private String showName;

}
