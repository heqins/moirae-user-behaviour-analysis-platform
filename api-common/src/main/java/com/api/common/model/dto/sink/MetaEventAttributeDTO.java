package com.api.common.model.dto.sink;

import lombok.Data;

@Data
public class MetaEventAttributeDTO {

    private String eventName;

    private String appId;

    private Integer status;

    private String dataType;

    private String attributeName;

}
