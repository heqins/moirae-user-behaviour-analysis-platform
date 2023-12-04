package com.admin.server.handler.analysis;

import com.api.common.enums.AnalysisCommandEnum;
import com.api.common.model.param.admin.AnalysisParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class BeanProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Map<AnalysisCommandEnum, Function<AnalysisParam, AnalysisHandler>> COMMAND_MAP = new HashMap<>();

    public static AnalysisHandler getByCommand(AnalysisCommandEnum commandType, AnalysisParam param) {
        return COMMAND_MAP.get(commandType).apply(param);
    }

    public AnalysisHandler processBeansImplementingInterface(String commandType) {
        Map<String, AnalysisHandler> beansOfType = applicationContext.getBeansOfType(AnalysisHandler.class);

        return beansOfType.getOrDefault(commandType, null);
    }
}
