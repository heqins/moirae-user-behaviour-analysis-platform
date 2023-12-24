package com.admin.server.facade;

import com.admin.server.handler.event.AnalysisHandler;
import com.admin.server.handler.event.BeanProcessor;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.admin.server.model.dto.RetentionAnalysisResultDto;
import com.api.common.enums.AnalysisCommandEnum;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Component
public class AnalysisFacade {

    private final Logger logger = LoggerFactory.getLogger(AnalysisFacade.class);

    @Resource
    private BeanProcessor beanProcessor;

    public EventAnalysisResultDto doEventAnalysis(AnalysisParam param) {
        AnalysisHandler eventHandler = beanProcessor.getByCommand(AnalysisCommandEnum.EVENT_COMMAND.getValue());
        if (eventHandler == null) {
            throw new IllegalStateException("分析类型不存在");
        }

        EventAnalysisResultDto eventResult = eventHandler.execute(param);
        logger.info("do event analysis result:{}", eventResult);

        return eventResult;
    }

    public List<EventCountDto> getEventCount(GetEventCountParam param) {
        AnalysisHandler eventHandler = beanProcessor.getByCommand(AnalysisCommandEnum.EVENT_COUNT_COMMAND.getValue());
        if (eventHandler == null) {
            throw new IllegalStateException("分析类型不存在");
        }

        List<EventCountDto> eventResult = eventHandler.getEventCount(param);

        return eventResult;
    }

    public RetentionAnalysisResultDto doRetentionAnalysis(AnalysisParam param) {
        AnalysisHandler eventHandler = beanProcessor.getByCommand(AnalysisCommandEnum.EVENT_RETENTION_COMMAND.getValue());
        if (eventHandler == null) {
            throw new IllegalStateException("分析类型不存在");
        }

        RetentionAnalysisResultDto eventResult = eventHandler.executeRetentionAnalysis(param);
        logger.info("do event analysis result:{}", eventResult);

        return null;
    }
}
