package com.admin.server.util;

import cn.hutool.core.lang.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtil {

    public static final String DECIMAL_PATTERN = "\\((\\d+),(\\d+)\\)";

    public static final String VARCHAR_PATTERN = "\\((\\d+)\\)";

    public static Pair<Integer, Integer> parseTypeNumber(String pattern, String text) {
        // 编译正则表达式
        Pattern regex = Pattern.compile(pattern);

        // 创建 Matcher 对象，并在文本中查找匹配项
        Matcher matcher = regex.matcher(text);
        // 查找匹配项并提取数字
        if (matcher.find()) {

            // 提取第一个捕获组中的数字（6）
            String number1Str = matcher.group(1);

            // 提取第二个捕获组中的数字（2）
            String number2Str = matcher.group(2);

            Pair<Integer, Integer> pair= new Pair<>(1, 1);
            return pair;
        }

        return null;
    }
}
