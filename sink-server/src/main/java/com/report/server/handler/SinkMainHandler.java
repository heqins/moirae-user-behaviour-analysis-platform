package com.report.server.handler;

import com.report.server.helper.RedisHelper;
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
