package com.admin.server.handler.event;

import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;

import java.util.List;

public interface AnalysisHandler {

    EventAnalysisResultDto execute(AnalysisParam param);

    List<EventCountDto> getEventCount(GetEventCountParam param);

}
