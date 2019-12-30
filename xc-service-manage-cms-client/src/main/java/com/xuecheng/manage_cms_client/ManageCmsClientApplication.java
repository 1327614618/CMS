package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages = {"com.xuecheng.framework"})
@ComponentScan(basePackages = {"com.xuecheng.manage_cms_client"})
@SpringBootApplication
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
