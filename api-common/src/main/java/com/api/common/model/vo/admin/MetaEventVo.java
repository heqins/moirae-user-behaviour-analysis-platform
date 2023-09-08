package com.api.common.model.vo.admin;

import lombok.Data;

@Data
public class MetaEventVo {

    private Long id;

    private String eventName;

    private String displayName;

    private Integer yesterdayCount;

}
