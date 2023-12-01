package com.admin.server.handler.analysis;

import com.admin.server.model.dto.EventAnalysisResultDto;
import com.api.common.model.param.admin.AnalysisParam;

public interface AnalysisHandler {

    EventAnalysisResultDto execute(AnalysisParam param);

}
