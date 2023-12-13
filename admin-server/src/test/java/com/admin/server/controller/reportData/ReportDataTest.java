package com.admin.server.controller.reportData;


import com.admin.server.facade.AnalysisFacade;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReportDataTest {

    @Resource
    private AnalysisFacade analysisFacade;

    @Test
    public void doAnalysis() {
        GetEventCountParam param = new GetEventCountParam();
        param.setAppId("test_app");
        param.setDateRange(List.of("2023-12-10", "2023-12-17"));

        analysisFacade.getEventCount(param);
    }

}
