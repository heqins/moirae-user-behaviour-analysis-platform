package com.admin.server.config;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    /**
     * MyBatisPlus拦截器（用于分页）
     */
//    @Bean
//    public MybatisPlusInterceptor paginationInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        //添加MySQL的分页拦截器
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }
}
