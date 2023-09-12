package com.admin.server.handler.analysis;

import com.api.common.enums.AnalysisCommandEnum;
import com.api.common.model.param.admin.AnalysisParam;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AnalysisHandlerFactory {

    private static final Map<AnalysisCommandEnum, Function<AnalysisParam, AnalysisHandler>> COMMAND_MAP = new HashMap<>();

    static {
        COMMAND_MAP.put(AnalysisCommandEnum.EVENT_COMMAND, EventHandler::new);
    }

    public static AnalysisHandler getByCommand(AnalysisCommandEnum commandType, AnalysisParam param) {
        return COMMAND_MAP.get(commandType).apply(param);
    }
}
