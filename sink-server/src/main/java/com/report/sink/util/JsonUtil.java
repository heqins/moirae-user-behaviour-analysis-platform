package com.report.sink.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonUtil {

    public static Set<String> getAllFieldNames(JSONObject jsonObject) {
        Set<String> fieldNames = new HashSet<>();

        // 遍历 JSON 对象
        traverse(jsonObject, fieldNames);

        return fieldNames;
    }

    private static void traverse(JSONObject jsonObject, Set<String> fieldNames) {
        for (String key : jsonObject.keySet()) {
            // 将字段名添加到 Set 中
            fieldNames.add(key);

            Object value = jsonObject.get(key);

            // 判断当前字段是否是 JSONObject 或 JSONArray
            if (value instanceof JSONObject) {
                traverse((JSONObject) value, fieldNames);
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (Object item : jsonArray) {
                    if (item instanceof JSONObject) {
                        traverse((JSONObject) item, fieldNames);
                    }
                }
            }
        }
    }

    public static Map<String, Object> getAllFieldsWithValues(JSONObject jsonObject) {
        Map<String, Object> fieldsWithValues = new HashMap<>();
        traverse(jsonObject, fieldsWithValues);
        return fieldsWithValues;
    }

    private static void traverse(JSONObject jsonObject, Map<String, Object> fieldsWithValues) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            // 将字段名和对应的值添加到 Map 中
            fieldsWithValues.put(key, value);

            // 判断当前字段是否是 JSONObject 或 JSONArray
            if (value instanceof JSONObject) {
                traverse((JSONObject) value, fieldsWithValues);
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (Object item : jsonArray) {
                    if (item instanceof JSONObject) {
                        traverse((JSONObject) item, fieldsWithValues);
                    }
                }
            }
        }
    }

    public static Object getNestedFieldValueRecursive(JSONObject jsonObject, String fieldName) {
        Object value = jsonObject.get(fieldName);
        if (value != null) {
            return value;
        }

        for (String key : jsonObject.keySet()) {
            Object temp = jsonObject.get(key);
            if (temp instanceof JSONObject) {
                Object nestedValue = getNestedFieldValueRecursive((JSONObject) temp, fieldName);
                if (nestedValue != null) {
                    return nestedValue;
                }
            } else if (temp instanceof JSONArray) {
                JSONArray nestedArray = (JSONArray) temp;
                if (!nestedArray.isEmpty()) {
                    Object nestedValue = nestedArray.get(0);
                    if (nestedValue instanceof JSONObject) {
                        return getNestedFieldValueRecursive((JSONObject) nestedValue, fieldName);
                    } else {
                        return nestedValue;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 递归方法检查是否存在指定字段
     * @param jsonObject
     * @param field
     * @return
     */
    public static boolean checkIfJsonFieldExist(JSONObject jsonObject, String field) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            // 判断当前字段是否是 JSONObject 或 JSONArray
            if (value instanceof JSONObject) {
                return checkIfJsonFieldExist((JSONObject) value, field);
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (Object item : jsonArray) {
                    if (item instanceof JSONObject) {
                       return checkIfJsonFieldExist((JSONObject) item, field);
                    }
                }
            }

            // 在这里判断是否包含 eventName 字段
            if (field.equals(key)) {
                return true;
            }
        }

        return false;
    }
}
