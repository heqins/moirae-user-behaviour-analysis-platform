package com.admin.server.handler.event;

import com.admin.server.helper.DorisHelper;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class EventCountHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventCountHandler.class);

    @Resource
    private DorisHelper dorisHelper;

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        return null;
    }

    @Override
    public EventCountDto getEventCount(GetEventCountParam param) {
        Pair<String, List<String>> eventSqlPair = generateCountSql(param);

        List<Map<String, Object>> countMap = dorisHelper.selectEventAnalysis(eventSqlPair);

        return null;
    }

    private Pair<String, List<String>> generateCountSql(GetEventCountParam param) {

    }
}
