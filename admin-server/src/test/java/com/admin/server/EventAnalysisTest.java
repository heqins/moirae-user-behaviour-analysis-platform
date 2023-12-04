package com.admin.server;


import com.admin.server.facade.AnalysisFacade;
import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EventAnalysisTest {

    @Resource
    private AnalysisFacade analysisFacade;

    @Test
    public void doAnalysis() {
        AnalysisParam param = new AnalysisParam();

        param.setAppId("2crdwf5q");
        param.setWindowFormat("按天");

        List<String> dateRange = new ArrayList<>();
        dateRange.add("2023-12-01");
        dateRange.add("2023-12-04");
        param.setDateRange(dateRange);

        AnalysisWhereFilterParam filterParam = new AnalysisWhereFilterParam();
        filterParam.setRelation("AND");
        AnalysisWhereFilterParam.Filter filter = new AnalysisWhereFilterParam.Filter();
        filter.setComparator("=");
        filter.setValue("test");
        filter.setColumnName("event_name");

        filterParam.setFilters(List.of(filter));
        param.setWhereFilter(filterParam);

        List<AnalysisAggregationParam> aggregationParams = new ArrayList<>();
        AnalysisAggregationParam agg = new AnalysisAggregationParam();
        AnalysisWhereFilterParam relation = new AnalysisWhereFilterParam();

        relation.setRelation("AND");
        AnalysisWhereFilterParam.Filter relationFilter = new AnalysisWhereFilterParam.Filter();
        relationFilter.setComparator("=");
        relationFilter.setValue("test");
        relationFilter.setColumnName("event_name");

        agg.setRelation(relation);
        agg.setEventName("test");
        agg.setType("zhibiao");
        agg.setEventNameForDisplay("默认");

        List<String> selectAttrs = new ArrayList<>();
        selectAttrs.add("默认");
        selectAttrs.add("A1");

        agg.setSelectAttributes(selectAttrs);

        aggregationParams.add(agg);

        param.setAggregations(aggregationParams);

        Assert.assertEquals("2crdwf5q", param.getAppId());

        analysisFacade.doEventAnalysis(param);
    }

}
