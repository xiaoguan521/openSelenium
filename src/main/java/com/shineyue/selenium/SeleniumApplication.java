package com.shineyue.selenium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeleniumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeleniumApplication.class, args);
    }

}
