package com.admin.server.handler.event;

import cn.hutool.json.JSONUtil;
import com.admin.server.helper.DorisHelper;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.admin.server.model.dto.RetentionAnalysisResultDto;
import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RetentionAnalysisHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(RetentionAnalysisHandler.class);

    @Resource
    private DorisHelper dorisHelper;

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        return null;
    }

    @Override
    public List<EventCountDto> getEventCount(GetEventCountParam param) {
        return null;
    }

    @Override
    public RetentionAnalysisResultDto executeRetentionAnalysis(AnalysisParam param) {

        Pair<String, List<String>> sqlPair = getRetentionExecuteSql(param);

        logger.info("RetentionAnalysisHandler sql: {} args:{}", sqlPair.getKey(), JSONUtil.toJsonStr(sqlPair.getValue()));

        List<Map<String, Object>> maps = dorisHelper.selectEventAnalysis(sqlPair);

        RetentionAnalysisResultDto resultDto = constructResult(maps);
        return resultDto;
    }

    private Pair<String, List<String>> getRetentionExecuteSql(AnalysisParam param) {
        List<String> sqlList = new ArrayList<>();
        List<String> sqlArgList = new ArrayList<>();

        List<String> fullDateRange = generateDateRange(param.getDateRange());
        for (String date: fullDateRange) {
            Pair<String, List<String>> sqlByDatePair = SqlUtil.getRetentionSqlByDate(date, param);
            if (sqlByDatePair.getKey() == null) {
                throw new IllegalArgumentException("sqlByDatePair is null");
            }

            sqlList.add(sqlByDatePair.getKey());
            sqlArgList.addAll(sqlByDatePair.getValue());
        }

        String finalSql = String.join(" union all ", sqlList);
        return Pair.of(finalSql, sqlArgList);
    }

    private RetentionAnalysisResultDto constructResult(List<Map<String, Object>> maps) {

        return null;
    }

    private List<String> generateDateRange(List<String> dateRangeParam) {
        if (CollectionUtils.isEmpty(dateRangeParam) || dateRangeParam.size() != 2) {
            throw new IllegalArgumentException("dateRangeParam非法!");
        }

        List<String> dateRange = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dateRangeParam.get(0), formatter);
        LocalDate endDate = LocalDate.parse(dateRangeParam.get(1), formatter);

        while (!startDate.isAfter(endDate)) {
            dateRange.add(startDate.format(formatter));
            startDate = startDate.plusDays(1);
        }

        return dateRange;
    }
}
