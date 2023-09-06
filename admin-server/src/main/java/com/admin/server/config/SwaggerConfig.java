package com.admin.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author heqin
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {

//    @Bean
//    public Docket docket() {
//        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo()).enable(true)
//                .select()
//                //apis： 添加swagger接口提取范围
//                .apis(RequestHandlerSelectors.basePackage("com.admin.server.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("编程猫学习网站的 admin 管理端 API")
//                .description("codingmore")
//                .contact(new Contact("codingmore", "http://www.codingmore.com", "codingmore@qq.com"))
//                .version("1.0")
//                .build();
//    }
}
