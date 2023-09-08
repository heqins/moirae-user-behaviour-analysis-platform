package com.admin.server.model.domain;

import com.admin.server.model.bo.MetaEvent;
import com.api.common.model.vo.admin.MetaEventVo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MetaEventUtil {


    public static List<MetaEventVo> transferFromEventBo(List<MetaEvent> metaEvents) {
        if (metaEvents.size() == 0) {
            return Collections.emptyList();
        }

        return metaEvents.stream().map(event -> {
            MetaEventVo vo = new MetaEventVo();
            vo.setEventName(event.getEventName());
            vo.setDisplayName(event.getShowName() != null ? event.getShowName() : event.getEventName());
            vo.setYesterdayCount(event.getYesterdayCount());
            vo.setId(event.getId());

            return vo;
        }).collect(Collectors.toList());
    }
}
