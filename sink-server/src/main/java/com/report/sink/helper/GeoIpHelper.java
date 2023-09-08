package com.report.sink.helper;

import cn.hutool.json.JSONUtil;
import com.api.common.model.dto.sink.GeoIpDTO;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Component
@Slf4j
public class GeoIpHelper {

    @Value("${geo-ip.file-path:}")
    private String geoIpFilePath;

    private DatabaseReader reader;

    @PostConstruct
    public void init() {
        File database = new File(geoIpFilePath);

        try {
            this.reader = new DatabaseReader.Builder(database).build();
        }catch (IOException e) {
            log.error("geoIp build reader error", e);
        }
    }

    public GeoIpDTO queryIp(String ip) {
        if (this.reader == null) {
            log.warn("geoIp reader null ip:{}", ip);
            return null;
        }

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = this.reader.country(ipAddress);

            Country country = response.getCountry();
            GeoIpDTO.GeoIpDTOBuilder builder = GeoIpDTO.builder();

            log.info("geoIp queryIp result:{}", JSONUtil.toJsonStr(country));
            return  builder.country(country.getName()).ip(ip).build();
        }catch (IOException | GeoIp2Exception e) {
            log.error("");
        }

        return null;
    }
}
