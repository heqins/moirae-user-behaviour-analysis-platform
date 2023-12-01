package com.admin.server.facade;

import com.admin.server.handler.analysis.AnalysisHandler;
import com.admin.server.handler.analysis.AnalysisHandlerFactory;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.api.common.enums.AnalysisCommandEnum;
import com.api.common.model.param.admin.AnalysisParam;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author heqin
 */
@Component
public class AnalysisFacade {

    public void doEventAnalysis(AnalysisParam param) {
        AnalysisHandler eventHandler = AnalysisHandlerFactory.getByCommand(AnalysisCommandEnum.EVENT_COMMAND, param);
        //todo:
        if (eventHandler == null) {
            throw new IllegalStateException("");
        }

        EventAnalysisResultDto eventResult = eventHandler.execute(param);

    }
}
