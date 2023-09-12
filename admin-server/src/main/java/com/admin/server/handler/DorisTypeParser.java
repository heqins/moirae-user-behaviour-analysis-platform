package com.admin.server.handler;

import cn.hutool.core.lang.Pair;

public interface DorisTypeParser {

    Pair<Integer, Integer> parseType(String text);

}
