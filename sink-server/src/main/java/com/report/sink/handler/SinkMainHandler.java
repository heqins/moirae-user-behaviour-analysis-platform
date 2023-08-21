package com.report.sink.handler;

import com.report.sink.helper.RedisHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@Component
public class SinkMainHandler {

    @Resource
    private RedisHelper redisHelper;


    public void main() {

    }
}
