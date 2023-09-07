package com.report.sink.config;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object o, Method method, Object... objects) {
        // 自定义缓存键生成逻辑，例如基于方法名和参数生成缓存键
        // 示例：方法名 + 第一个参数
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());
        for (int i = 0; i < objects.length; i++) {
            sb.append(String.valueOf(objects[i]));
            if (i != objects.length - 1) {
                sb.append(":");
            }
        }

        return sb.toString();
    }
}
