package com.shineyue.selenium.utils;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouzhiqiang
 * @version 1.0
 * @date 2022-09-23 15:56
 */
public class WebDriverPoolTest{

    @Autowired
    private WebDriverManager webDriverManager;

    public void test() {
        try {
            List<Thread> threadlist = new ArrayList<Thread>();
            for (int i = 1; i <= 9; i++) {
                Thread subThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WebDriver webDriver = webDriverManager.getWebDriver();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        webDriverManager.closeWebDriver(webDriver);
                    }
                }, "thread name" + i);
                subThread.start();
                threadlist.add(subThread);
            }
            Thread.sleep(10000);
            webDriverManager.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
