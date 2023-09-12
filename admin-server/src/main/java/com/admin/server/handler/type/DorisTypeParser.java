package com.admin.server.handler.type;

import cn.hutool.core.lang.Pair;

public interface DorisTypeParser {

    Pair<Integer, Integer> parseType(String text);

}
