package com.admin.server.facade;

import com.admin.server.handler.analysis.AnalysisHandler;
import com.admin.server.handler.analysis.BeanProcessor;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.api.common.enums.AnalysisCommandEnum;
import com.api.common.model.param.admin.AnalysisParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@Component
public class AnalysisFacade {

    private final Logger logger = LoggerFactory.getLogger(AnalysisFacade.class);

    @Resource
    private BeanProcessor beanProcessor;

    public void doEventAnalysis(AnalysisParam param) {
        AnalysisHandler eventHandler = beanProcessor.getByCommand(AnalysisCommandEnum.EVENT_COMMAND.getValue());
        if (eventHandler == null) {
            throw new IllegalStateException("");
        }

        EventAnalysisResultDto eventResult = eventHandler.execute(param);
        logger.info("do event analysis result:{}", eventResult);
    }
}
