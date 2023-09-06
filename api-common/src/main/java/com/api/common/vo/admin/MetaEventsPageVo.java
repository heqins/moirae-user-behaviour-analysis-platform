package com.api.common.vo.admin;

import com.api.common.vo.BasePageVo;
import lombok.Data;

import java.util.List;

@Data
public class MetaEventsPageVo extends BasePageVo {

    private String appId;

    private List<MetaEventVo> events;
}
