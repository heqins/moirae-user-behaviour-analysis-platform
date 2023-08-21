package com.report.sink;

import com.report.sink.helper.RedisHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Resource
    private RedisHelper redisHelper;

    @Test
    public void testGet() {
        List<String> hashValues = redisHelper.getHashValues("szt:json:raw:v2:130");
        System.out.println("test");
    }
}
