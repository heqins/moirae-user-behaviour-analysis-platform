package com.admin.server.handler.event;

import com.admin.server.helper.DorisHelper;
import com.admin.server.model.bo.App;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.model.dto.EventCountDto;
import com.admin.server.service.IAppService;
import com.admin.server.service.IMetaEventService;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.reportData.GetEventCountParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EventCountHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventCountHandler.class);

    private final String SUCCESS  = "success";
    private final String FAIL = "fail";

    private final String ALL = "all";

    @Resource
    private DorisHelper dorisHelper;

    @Resource
    private IAppService appService;

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        return null;
    }

    @Override
    public List<EventCountDto> getEventCount(GetEventCountParam param) {
        App app = appService.getByAppID(param.getAppId());
        if (app == null) {
            throw new IllegalArgumentException("应用不存在");
        }

        List<Pair<String, List<String>>> eventSqlPairList = generateCountSql(param);

        List<Map<String, Object>> allCountMap = dorisHelper.selectEventAnalysis(eventSqlPairList.get(0));
        if (CollectionUtils.isEmpty(allCountMap)) {
            return new ArrayList<>();
        }

        List<String> eventNames = allCountMap.stream().map(map -> (String) map.get("event_name")).collect(Collectors.toList());

        Map<String, Map<String, Long>> countMap = new HashMap<>();
        fillCountMap(countMap, allCountMap, ALL);

        List<Map<String, Object>> successCountMap = dorisHelper.selectEventAnalysis(eventSqlPairList.get(1));
        fillCountMap(countMap, successCountMap, SUCCESS);

        List<Map<String, Object>> failCountMap = dorisHelper.selectEventAnalysis(eventSqlPairList.get(2));
        fillCountMap(countMap, failCountMap, FAIL);

        // 根据allCountMap，successCountMap，failCountMap中的event_name和count封装一个EventCountDto对象

        List<EventCountDto> result = new ArrayList<>(countMap.size());
        for (Map.Entry<String, Map<String, Long>> entry: countMap.entrySet()) {
            EventCountDto dto = new EventCountDto();

            dto.setTotal(entry.getValue().getOrDefault(ALL, 0L));
            dto.setSuccess(entry.getValue().getOrDefault(SUCCESS, 0L));
            dto.setFail(entry.getValue().getOrDefault(FAIL, 0L));
            dto.setEventName(entry.getKey());

            result.add(dto);
        }

        return result;
    }

    private void fillCountMap(Map<String, Map<String, Long>> countMap, List<Map<String, Object>> mapList, String type) {
        for (Map<String, Object> entry: mapList) {
            String eventName = (String) entry.get("event_name");
            Long count = (Long) entry.get("count");

            if (!countMap.containsKey(eventName)) {
                countMap.put(eventName, new HashMap<>());
            }

            Map<String, Long> eventCountMap = countMap.get(eventName);
            if (!eventCountMap.containsKey(type)) {
                eventCountMap.put(type, count);
            }
        }
    }

    private List<Pair<String, List<String>>> generateCountSql(GetEventCountParam param) {
        List<String> dates = param.getDateRange();
        if (CollectionUtils.isEmpty(dates) || dates.size() < 2) {
            throw new IllegalArgumentException("日期范围不合法");
        }

        List<String> args = new ArrayList<>(3);
        args.add(param.getAppId());
        args.add(dates.get(0));
        args.add(dates.get(1));

        String allCountSql = "SELECT event_name, count(*) as count FROM event_log where app_id = ?" +
                " and event_date >= ? and event_date <= ? group by event_name;";

        String successCountSql = "SELECT event_name, count(*) as count FROM event_log where app_id = ?" +
                " and status = 1 and event_date >= ? and event_date <= ? group by event_name;";

        String failCountSql = "SELECT event_name, count(*) as count FROM event_log where app_id = ?\" +\n" +
                " and status = 0 and event_date >= ? and event_date <= ? group by event_name;";

        List<Pair<String, List<String>>> result = new ArrayList<>(3);
        result.add(Pair.of(allCountSql, args));
        result.add(Pair.of(successCountSql, args));
        result.add(Pair.of(failCountSql, args));
        return result;
    }
}
