package com.api.common.model.vo.admin;

import lombok.Data;

import java.util.List;

@Data
public class MetaEventAttributePageVo {

    private String eventName;

    private List<MetaEventAttributeVo> attributes;

}
