package com.shineyue.selenium.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author zhouzhiqiang
 * @version 1.0
 * @date 2022-09-23 15:37
 */
@Data
@Component
@ConfigurationProperties(prefix = "shineyue.webdriver", ignoreInvalidFields = true)
public class WebDriverBean {

    /**
     * 浏览器驱动路径
     */
    private String driverPath;

    /**
     * chrome路径
     */
    private String chromepath;

    /**
     * 连接池最大连接数
     */
    private int maxConnections ;
    /**
     * 连接池最小连接数
     */
    private int minConnections;
    /**
     * 连接池初始连接数
     */
    private int initConnections;
    /**
     * 重连间隔时间 ，单位毫秒
     */
    private int conninterval ;
    /**
     * 获取连接超时时间 ，单位毫秒，0永不超时
     */
    private int timeout ;

    /**
     * 远程连接地址 ，单位毫秒，0永不超时
     */
    private String remoteAddr ;
}
