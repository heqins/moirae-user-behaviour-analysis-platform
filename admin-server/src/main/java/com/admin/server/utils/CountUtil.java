package com.admin.server.utils;

import com.admin.server.enums.CountType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author heqin
 */
public class CountUtil {
    public static final Map<String, String> INT_PROP_QUOTAS = new HashMap<>();

    static {
        INT_PROP_QUOTAS.put(CountType.AllSum.name(), "总和");
        INT_PROP_QUOTAS.put(CountType.AvgCount.name(), "均值");
        INT_PROP_QUOTAS.put(CountType.AvgSumByUser.name(), "人均值");
        INT_PROP_QUOTAS.put(CountType.MaxCount.name(), "最大值");
        INT_PROP_QUOTAS.put(CountType.MinCount.name(), "最小值");
        INT_PROP_QUOTAS.put(CountType.DistincCount.name(), "去重数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount.name(), "中位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount5.name(), "5分位数");
    }

    public static final Map<String, String> STRING_PROP_QUOTAS = new HashMap<>();

    static {
        STRING_PROP_QUOTAS.put(CountType.DistincCount.name(), "去重数");
    }

    public interface GetCountCol {
        String apply(String col);
    }

    public static final String SPLIT = "$$$xwl$$$";
    public static final String Default = "默认";

    public static final Map<String, GetCountCol> COUNT_TYPE_MAP = new HashMap<>();

    static {
        COUNT_TYPE_MAP.put(CountType.AllCount.getType(), col -> {
            if (col.equals(Default)) {
                return toString("count(*)");
            }

            return toString(String.format("count(%s)", col));
        });

        COUNT_TYPE_MAP.put(CountType.AllSum.getType(), col -> toString((String.format("sum(%s)", col))));

        COUNT_TYPE_MAP.put(CountType.AvgCount.getType(), col -> toString((String.format("avg(%s)", col))));

        COUNT_TYPE_MAP.put(CountType.AvgSumByUser.getType(), col -> toString((String.format("SUM(%s)/COUNT(DISTINCT unique_id)", col))));

        COUNT_TYPE_MAP.put(CountType.MiddleCount.getType(), col -> toString(String.format("percentile(%s, 0.5)", col)));

        COUNT_TYPE_MAP.put(CountType.MaxCount.getType(), col -> toString((String.format("max(%s)", col))));

        COUNT_TYPE_MAP.put(CountType.MinCount.getType(), col -> toString((String.format("min(%s)", col))));

        COUNT_TYPE_MAP.put(CountType.DistincCount.getType(), col -> toString((String.format("count(DISTINCT %s)", col))));
    }

    private static String clickUserNum(String col) {
        if (col.equals(Default)) {
            return toString("COUNT(DISTINCT unique_id)");
        }

        return toString(String.format("COUNT(DISTINCT %s)", col));
    }
    
    private static String toString(String fn) {
        return String.format("CAST(%s as VARCHAR(256))", fn);
    }
}