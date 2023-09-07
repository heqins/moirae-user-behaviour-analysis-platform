package com.api.common.vo.admin;

import com.api.common.bo.MetaEvent;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MetaEventVo {

    private Long id;

    private String eventName;

    private String displayName;

    private Integer yesterdayCount;

    public static List<MetaEventVo> transferFromEventBo(List<MetaEvent> metaEvents) {
        if (metaEvents.size() == 0) {
            return Collections.emptyList();
        }

        return metaEvents.stream().map(event -> {
            MetaEventVo vo = new MetaEventVo();
            vo.setEventName(event.getEventName());
            vo.setDisplayName(event.getShowName() != null ? event.getShowName() : event.getEventName());
            vo.setYesterdayCount(event.getYesterdayCount());

            return vo;
        }).collect(Collectors.toList());
    }
}
