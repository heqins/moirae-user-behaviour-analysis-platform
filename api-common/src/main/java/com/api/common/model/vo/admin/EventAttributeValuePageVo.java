package com.api.common.model.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * @author heqin
 */
@Data
public class EventAttributeValuePageVo {

    private String appId;

    private String eventName;

    private String attributeName;

    private List<String> values;

}
