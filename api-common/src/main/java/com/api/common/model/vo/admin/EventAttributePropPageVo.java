package com.api.common.model.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * @author heqin
 */
@Data
public class EventAttributePropPageVo {

    private String appId;

    private String eventName;

    private List<EventAttributePropVo> props;

}
