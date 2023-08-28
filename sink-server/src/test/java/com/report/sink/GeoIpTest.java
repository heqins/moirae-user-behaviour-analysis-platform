package com.report.sink;

import cn.hutool.json.JSONUtil;
import com.api.common.dto.GeoIpDTO;
import com.report.sink.helper.GeoIpHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class GeoIpTest {

    @Resource
    private GeoIpHelper geoIpHelper;

    @Test
    public void queryIp() {
        GeoIpDTO geoIpDTO = geoIpHelper.queryIp("114.93.1.44");
        assert geoIpDTO != null;

        log.info("geoIpDto:{}", JSONUtil.toJsonStr(geoIpDTO));
    }
}
