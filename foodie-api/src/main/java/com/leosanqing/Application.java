package com.leosanqing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @Author: leosanqing
 * @Date: 2019-12-03 23:24
 */
@SpringBootApplication
// 扫描通用mapper配置
@MapperScan(basePackages = "com.leosanqing.mapper")
// 扫描组件包
@ComponentScan(basePackages = {"com.leosanqing","org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
//        new SpringApplicationBuilder().web(WebApplicationType.SERVLET).run(args);
    }
}
