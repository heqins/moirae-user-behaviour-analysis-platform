package com.report.sink.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.report.sink.properties.DataSourceProperty;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.report.sink.mapper.doris",sqlSessionFactoryRef = "dorisSqlSessionFactory")
public class DorisDataSourceConfig {

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Primary
    @Bean(name = "dorisDataSource")
    public DataSource masterDataSource(){
        DataSourceProperty.DorisConfig dorisConfig = dataSourceProperty.getDoris();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dorisConfig.getUrl());
        config.setUsername(dorisConfig.getUsername());
        config.setPassword(dorisConfig.getPassword());
        config.setDriverClassName(dorisConfig.getDriver());

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
