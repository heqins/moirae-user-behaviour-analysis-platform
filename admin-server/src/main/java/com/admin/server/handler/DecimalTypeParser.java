package com.admin.server.handler;

import cn.hutool.core.lang.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalTypeParser implements DorisTypeParser{

    private static final String DECIMAL_PATTERN = "\\((\\d+),(\\d+)\\)";

    @Override
    public Pair<Integer, Integer> parseType(String text) {
        // 编译正则表达式
        Pattern regex = Pattern.compile(DECIMAL_PATTERN);

        // 创建 Matcher 对象，并在文本中查找匹配项
        Matcher matcher = regex.matcher(text);

        // 查找匹配项并提取数字
        if (matcher.find()) {
            // 提取第一个捕获组中的数字（6）
            String number1Str = matcher.group(1);

            String number2Str = matcher.group(2);

            return new Pair<>(Integer.parseInt(number1Str), Integer.parseInt(number2Str));
        }

        return null;
    }
}
