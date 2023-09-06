package com.api.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class MetaEventsPageVo extends BasePageVo{

    private String appId;

    private List<MetaEventVo> events;
}
