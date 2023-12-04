package com.admin.server.handler.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    public AnalysisHandler getByCommand(String commandType) {
        Map<String, AnalysisHandler> beansOfType = applicationContext.getBeansOfType(AnalysisHandler.class);

        return beansOfType.getOrDefault(commandType, null);
    }
}
