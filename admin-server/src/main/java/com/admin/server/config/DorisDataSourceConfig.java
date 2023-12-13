package com.admin.server.config;

import com.admin.server.properties.DorisProrperties;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.admin.server.mapper.doris",sqlSessionFactoryRef = "dorisSqlSessionFactory")
public class DorisDataSourceConfig {

    @Resource
    private DorisProrperties dataSourceProperty;

    @Primary
    @Bean(name = "dorisDataSource")
    public DataSource masterDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSourceProperty.getUrl());
        config.setUsername(dataSourceProperty.getUsername());
        config.setPassword(dataSourceProperty.getPassword());
        config.setDriverClassName(dataSourceProperty.getDriverClassName());

        config.setMinimumIdle(10);
        config.setMaximumPoolSize(30);
        config.setConnectionTimeout(10000);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(540000);
        config.setIdleTimeout(500000);

        return new HikariDataSource(config);
    }

    @Bean(name = "dorisSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dorisDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        return sessionFactoryBean.getObject();
    }
}
