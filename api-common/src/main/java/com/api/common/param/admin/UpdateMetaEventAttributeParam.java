package com.api.common.param.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author heqin
 */
@Data
@ApiModel(description = "更改元事件属性参数")
public class UpdateMetaEventAttributeParam {

    @ApiParam(value = "应用id", required = true)
    @NotBlank
    private String appId;

    @ApiParam(value = "事件名称", required = true)
    @NotBlank
    private String eventName;

    private String attributeName;

    private Integer status;

    private String dataType;

    private Integer length;

    private Integer limit;

    private String showName;

}
