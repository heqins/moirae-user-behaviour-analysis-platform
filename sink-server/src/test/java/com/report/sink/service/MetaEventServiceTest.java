package com.report.sink.service;

import cn.hutool.json.JSONUtil;
import com.report.sink.model.bo.MetaEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MetaEventServiceTest {

    @Resource
    private IMetaEventService metaEventService;

    @Test
    public void testMetaEventGet() {
        String appId = "f67b5cd6-a12e-442c-9dba-833e9d6a04f2";
        String eventName = "login";

        MetaEvent metaEvent = metaEventService.getMetaEvent(appId, eventName);
        assert metaEvent != null;

        //log.info("metaEvent:{}", JSONUtil.toJsonStr(metaEvent));
    }
}
