package com.api.common.model.param.admin.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "重置应用密钥")
public class ResetKeyParam {

    @Schema(description = "应用id", required = true)
    @NotBlank
    private String appId;


}
