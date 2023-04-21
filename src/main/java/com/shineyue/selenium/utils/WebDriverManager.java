package com.shineyue.selenium.utils;


import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author zhouzhiqiang
 * @version 1.0
 * @date 2022-09-23 15:36
 */
@Component
public class WebDriverManager implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverManager.class);

    @Autowired
    private WebDriverBean webDriverBean;

    private static volatile WebDriverPool webDriverPool = null;;

    private LocalDateTime lastDestroyTime = LocalDateTime.now();

    public WebDriverPool createWebDriverPool() {
        if (webDriverPool == null) {
            synchronized (WebDriverManager.class) {
                if (webDriverPool == null) {
                    webDriverPool = WebDriverPool.createWebDriverPool(webDriverBean);
                }
            }
        }
        return webDriverPool;
    }

    public WebDriver getWebDriver() {
        if (webDriverPool == null) {
            WebDriverPool driverPool = this.createWebDriverPool();
            driverPool.checkPool();
        }
        WebDriver webDriver = null;
        synchronized (WebDriverManager.class) {
            webDriver = webDriverPool.getWebDriver();
            if (ObjectUtils.isEmpty(webDriver)) {
                destroy();
                createWebDriverPool();
            }
//            webDriver = webDriverPool.getWebDriver();
        }
        return webDriver;
    }

    public void closeWebDriver(WebDriver driver) {
        if (driver != null && webDriverPool != null) {
            webDriverPool.releaseWebDriver(driver);
        }
    }
@Override
    public void destroy() {
        logger.info("临终执行");
    webDriverPool.destroy();
    webDriverPool = null;
        //每分钟只允许销毁一次
        /*if (webDriverPool != null && Duration.between(lastDestroyTime, LocalDateTime.now()).getSeconds() > 60) {
            synchronized (WebDriverManager.class) {
                if (webDriverPool != null && Duration.between(lastDestroyTime, LocalDateTime.now()).getSeconds() > 60) {
                    try {
                        webDriverPool.destroy();
                        webDriverPool = null;
                    } finally {
                        lastDestroyTime = LocalDateTime.now();
                    }
                }
            }
        }*/
    }

}

