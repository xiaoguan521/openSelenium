package com.shineyue.selenium.utils;

import org.openqa.selenium.WebDriver;

/**
 * @author zhouzhiqiang
 * @version 1.0
 * @date 2022-09-23 15:33
 */
public interface IWebDriverPool {

    /**
     * 获取一个浏览器驱动，如果等待超过超时时间，将返回null
     *
     * @return 浏览器驱动对象
     */
    WebDriver getWebDriver();

    /**
     * 获得当前线程的连接库连接
     *
     * @return 浏览器驱动对象
     */
    WebDriver getCurrentConnecton();

    /**
     * 释放当前线程浏览器驱动
     *
     * @param driver 浏览器驱动对象
     */
    void releaseWebDriver(WebDriver driver);

    /**
     * 销毁清空当前驱动连接池
     */
    void destroy();

    /**
     * 连接池可用状态
     *
     * @return 连接池是否可用
     */
    boolean isActive();

    /**
     * 定时器，检查连接池
     */
    void checkPool();

    /**
     * 获取线程池活动连接数
     *
     * @return 线程池活动连接数
     */
    int getActiveNum();

    /**
     * 获取线程池空闲连接数
     *
     * @return 线程池空闲连接数
     */
    int getFreeNum();

}
