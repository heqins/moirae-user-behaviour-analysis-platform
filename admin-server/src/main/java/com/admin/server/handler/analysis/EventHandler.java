package com.admin.server.handler.analysis;

import cn.hutool.json.JSONUtil;
import com.admin.server.helper.DorisHelper;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heqin
 */
@Component
public class EventHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Resource
    private DorisHelper dorisHelper;

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        Pair<String, List<String>> sqlPair = getEventSql(param);
        logger.info("EventHandler sql: {} args:{}", sqlPair.getKey(), JSONUtil.toJsonStr(sqlPair.getValue()));

        List<Map<String, Object>> maps = dorisHelper.selectEventAnalysis(sqlPair);

        EventAnalysisResultDto resultDto = constructResult(maps);

        return resultDto;
    }

    private EventAnalysisResultDto constructResult(List<Map<String, Object>> dataMaps) {
        EventAnalysisResultDto resultDto = new EventAnalysisResultDto();

        List<EventAnalysisResultDto.DataGroupDto> groupDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dataMaps)) {
            for (Map<String, Object> dataMap : dataMaps) {
                EventAnalysisResultDto.DataGroupDto groupDto = new EventAnalysisResultDto.DataGroupDto();
                groupDto.setItem(dataMap);

                groupDtos.add(groupDto);
            }
        }

        resultDto.setDataGroups(groupDtos);
        resultDto.setTotal(dataMaps.size());

        return resultDto;
    }

    private Pair<String, List<String>> getEventSql(AnalysisParam param) {
        if (!param.isValid()) {
            throw new IllegalStateException("参数错误");
        }

        Pair<String, List<String>> whereSqlPair = SqlUtil.getWhereSql(param.getWhereFilter());
        List<String> sqlArgs = whereSqlPair.getValue();

        Pair<String, List<String>> dateRangeSqlPair = SqlUtil.getDateRangeSql(param.getDateRange());

        sqlArgs.addAll(dateRangeSqlPair.getValue());

        String sql = whereSqlPair.getKey() + dateRangeSqlPair.getKey();

        List<String> allArgs = new ArrayList<>();
        List<String> allSql = new ArrayList<>();

        for (int i = 0; i < param.getAggregations().size(); i++) {
            Pair<String, List<String>> sqlPair = SqlUtil.getAggregation(i, param, sqlArgs, sql);

            allSql.add(sqlPair.getKey());
            allArgs.addAll(sqlPair.getValue());
        }

        List<String> orderBy = new ArrayList<>();
        // order by
        if (param.getGroupBy() != null && param.getGroupBy().size() > 0) {
            orderBy.addAll(param.getGroupBy());
        }else {
            orderBy.add("serial_number");
        }

        String finalSql = String.format("select * from (%s) AS external order by %s",
                String.join(" union all ", allSql),
                String.join(", ", orderBy));

        return Pair.of(finalSql, allArgs);
    }
}
