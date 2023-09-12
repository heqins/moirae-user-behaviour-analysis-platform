package com.api.common.model.vo.admin;

import lombok.Data;

import java.util.List;

@Data
public class MetaEventsPageVo {

    private String appId;

    private List<MetaEventVo> events;

}
