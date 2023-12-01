package com.admin.server.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CountUtil {
    public interface GetCountCol {
        String apply(String col);
    }

    public static final String SPLIT = "$$$xwl$$$";
    public static final String Default = "默认";

    public enum CountType {
        UserNum, AllSum, AvgCount, AvgSumByUser, MiddleCount, MaxCount, MinCount,
        DistincCount, AllCount, ClickUserNum, AvgCountByUser, MiddleCount5,
        MiddleCount10, MiddleCount20, MiddleCount25, MiddleCount30, MiddleCount40,
        MiddleCount60, MiddleCount70, MiddleCount75, MiddleCount80, MiddleCount90,
        MiddleCount95, MiddleCount99
    }

    public enum ScaleType {
        TwoDecimalPlaces, Percentage, Rounding
    }

    private static final AtomicInteger autoAddId = new AtomicInteger(0);

    public static final Map<CountType, GetCountCol> COUNT_TYPE_MAP = new HashMap<>();

    static {
        COUNT_TYPE_MAP.put(CountType.UserNum, col -> {
            if (col.equals(Default)) {
                return toString("count()");
            }
            return toString(String.format("count(%s)", col));
        });

        COUNT_TYPE_MAP.put(CountType.AllSum, col -> toString(Round(NaN2Zero(String.format("sum(%s)", col)))));

        COUNT_TYPE_MAP.put(CountType.AvgCount, col -> toString(Round(NaN2Zero(String.format("avg(%s)", col)))));

        COUNT_TYPE_MAP.put(CountType.AvgSumByUser, col -> toString(Round(NaN2Zero(String.format("SUM(%s)/COUNT(DISTINCT xwl_distinct_id)", col)))));

        COUNT_TYPE_MAP.put(CountType.MiddleCount, col -> toString(NaN2Zero(String.format("quantile(%s)", col))));

        COUNT_TYPE_MAP.put(CountType.MaxCount, col -> toString(Round(NaN2Zero(String.format("max(%s)", col)))));

        COUNT_TYPE_MAP.put(CountType.MinCount, col -> toString(Round(NaN2Zero(String.format("min(%s)", col)))));

        COUNT_TYPE_MAP.put(CountType.DistincCount, col -> toString(Round(NaN2Zero(String.format("count(DISTINCT %s)", col)))));

        COUNT_TYPE_MAP.put(CountType.AllCount, CountTypeMap::allCount);
        COUNT_TYPE_MAP.put(CountType.ClickUserNum, CountTypeMap::clickUserNum);

        COUNT_TYPE_MAP.put(CountType.AvgCountByUser, col -> {
            if (col.equals(Default)) {
                return toString(Round(NaN2Zero(String.format("%s/%s", ToFloat32OrZero(allCount(col)), ToFloat32OrZero(clickUserNum(col))))));
            }

            String[] arr = col.split(SPLIT);
            return toString(Round(NaN2Zero(String.format("%s/%s", ToFloat32OrZero(allCount(arr[0])), ToFloat32OrZero(clickUserNum(arr[1]))))));
        });

        COUNT_TYPE_MAP.put(CountType.MiddleCount5, col -> toString(getQuantile(0.05, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount10, col -> toString(getQuantile(0.1, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount20, col -> toString(getQuantile(0.2, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount25, col -> toString(getQuantile(0.25, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount30, col -> toString(getQuantile(0.3, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount40, col -> toString(getQuantile(0.4, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount60, col -> toString(getQuantile(0.6, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount70, col -> toString(getQuantile(0.7, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount75, col -> toString(getQuantile(0.75, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount80, col -> toString(getQuantile(0.8, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount90, col -> toString(getQuantile(0.9, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount95, col -> toString(getQuantile(0.95, col)));
        COUNT_TYPE_MAP.put(CountType.MiddleCount99, col -> toString(getQuantile(0.99, col)));
    }

    private static String getQuantile(double sum, String col) {
        return NaN2Zero(String.format(" quantile(%s)(%s) ", sum, col));
    }

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
        INT_PROP_QUOTAS.put(CountType.MiddleCount10.name(), "10分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount20.name(), "20分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount25.name(), "25分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount30.name(), "30分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount40.name(), "40分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount60.name(), "60分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount70.name(), "70分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount75.name(), "75分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount80.name(), "80分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount90.name(), "90分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount95.name(), "95分位数");
        INT_PROP_QUOTAS.put(CountType.MiddleCount99.name(), "99分位数");
    }

    public static final Map<String, String> STRING_PROP_QUOTAS = new HashMap<>();

    static {
        STRING_PROP_QUOTAS.put(CountType.DistincCount.name(), "去重数");
    }

    private static String allCount(String col) {
        if (col.equals(Default)) {
            return toString("count()");
        }return toString(String.format("count(%s)", col));
    }

    private static String clickUserNum(String col) {
        if (col.equals(Default)) {
            return toString(Round(NaN2Zero("COUNT(DISTINCT xwl_distinct_id)")));
        }

        return toString(Round(NaN2Zero(String.format("COUNT(DISTINCT %s)", col))));
    }

    private static String NaN2Zero(String fn) {
        int autoAddIdValue = autoAddId.incrementAndGet();
        String uuid = "col_" + Integer.toString(autoAddIdValue);
        return String.format("if(isInfinite(if(isNaN(%s as %s),0,%s)),0,%s)", fn, uuid, uuid, uuid);
    }

    private static String toString(String fn) {
        return String.format("toString(%s)", fn);
    }

    private static String Round(String fn) {
        return String.format("round(%s,2)", fn);
    }

    private static String ToFloat32OrZero(String fn) {
        return String.format("toFloat64OrZero(CAST(%s,'String'))", fn);
    }

    // Additional utility methods can be added here as needed

    // Example of how to use the CountTypeMap
    public static void main(String[] args) {
        String columnName = "example_column";
        GetCountCol countFunction = COUNT_TYPE_MAP.get(CountType.UserNum);
        String result = countFunction.apply(columnName);
        System.out.println("Result: " + result);
    }
}