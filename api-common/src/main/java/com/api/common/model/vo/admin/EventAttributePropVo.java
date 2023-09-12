package com.api.common.model.vo.admin;

import lombok.Data;

import java.util.List;

@Data
public class EventAttributePropVo {

    private String displayName;

    private String attributeName;

    private String dataTypeName;

    private List<AttributePropTypeVo> types;

}
