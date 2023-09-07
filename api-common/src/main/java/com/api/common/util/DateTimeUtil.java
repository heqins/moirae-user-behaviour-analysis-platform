package com.api.common.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtil {

    public static Long toEpoch(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
