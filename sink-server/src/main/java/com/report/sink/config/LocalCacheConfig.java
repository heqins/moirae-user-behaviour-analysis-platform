package com.report.sink.config;

import com.api.common.dto.TableColumnDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author heqin
 */
@Configuration
public class LocalCacheConfig {

    @Bean(name = "columnLocalCache")
    public Cache<String, List<TableColumnDTO>> columnlocalCache() {
        return Caffeine.newBuilder().maximumSize(1000).build();
    }


}
