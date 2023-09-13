package com.report.sink.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.report.sink.properties.DataSourceProperty;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.report.sink.mapper.mysql",sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MySqlDataSourceConfig {

    @Resource
    private DataSourceProperty dataSourceProperty;

    @Bean(name = "mysqlDataSource")
    public DataSource masterDataSource(){
        DataSourceProperty.MysqlConfig mysqlConfig = dataSourceProperty.getMysql();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlConfig.getUrl());
        config.setUsername(mysqlConfig.getUsername());
        config.setPassword(mysqlConfig.getPassword());
        config.setDriverClassName(mysqlConfig.getDriver());

        config.setMinimumIdle(10);
        config.setMaximumPoolSize(30);
        config.setConnectionTimeout(10000);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(540000);
        config.setIdleTimeout(500000);

        return new HikariDataSource(config);
    }

    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        return sessionFactoryBean.getObject();
    }
}
