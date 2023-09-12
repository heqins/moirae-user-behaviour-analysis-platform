package com.admin.server.handler.analysis;

import com.api.common.model.param.admin.AnalysisParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private final AnalysisParam param;

    public EventHandler(AnalysisParam param) {
        this.param = param;
    }

    @Override
    public void execute() {

    }
}
