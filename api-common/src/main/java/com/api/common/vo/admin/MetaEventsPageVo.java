package com.api.common.vo.admin;

import lombok.Data;

import java.util.List;

@Data
public class MetaEventsPageVo {

    private String appId;

    private List<MetaEventVo> events;
}
