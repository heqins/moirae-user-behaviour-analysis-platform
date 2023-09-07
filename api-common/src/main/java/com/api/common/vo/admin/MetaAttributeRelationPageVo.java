package com.api.common.vo.admin;

import lombok.Data;

import java.util.List;

@Data
public class MetaAttributeRelationPageVo {

    private String eventName;

    private List<MetaAttributeRelationVo> attributes;

}
