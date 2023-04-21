package com.shineyue.selenium.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

/**
 * Author liuXiaoChen
 * Date 2022-11-15 - 15:23
 * Description 工具类
 */
@Slf4j
@Component
public class Tools {
    @Autowired
    WebDriverManager webDriverManager;

    /**
     * 截图
     *
     * @param domain          地址  https://www.baidu.com/
     * @param correlationId   图片附件保留关联id
     * @param rollingDistance 滚动距离 300
     * @param waitTime        滚动等待时间(毫秒) 500
     * @param implicitlyWait  浏览器等待时间(秒)
     * @return void
     * @date 2020/9/28 10:48
     * @author Dora
     **/
    public double screenshot(String domain, Long correlationId
            , int rollingDistance, long waitTime, long implicitlyWait, String fileName) {
        Assert.hasText(domain, "域名不能为空");
        Assert.hasText(String.valueOf(correlationId), "关联id不能为空");
        long o = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("获取连接");
        WebDriver driver =webDriverManager.getWebDriver();
                stopWatch.stop();
                log.info("连接获取已经完成了");
        stopWatch.start("页面处理");
        try {
            Duration duration = Duration.ofMillis(implicitlyWait * 1000);
            driver.manage().timeouts().implicitlyWait(duration);
            driver.manage().window().maximize();
            driver.get(domain);
            //等待页面加载完成
            new WebDriverWait(driver, Duration.ofMillis(300)).until(drive -> ((JavascriptExecutor) drive)
                    .executeScript("return document.readyState").equals("complete"));
            // 设置小的分辨率
            driver.manage().window().setSize(new Dimension(1920, 1080));
            JavascriptExecutor je = (JavascriptExecutor) driver;
            int height = Integer.parseInt(je.executeScript("return document.body.scrollHeight") + "");
            log.info("{},当前页面高度:{}", domain, height);
            // 滚动次数
            int frequency = height % rollingDistance == 0 ? height / rollingDistance : height / rollingDistance + 1;
            for (int i = 0; i < frequency; i++) {
                int length = i * rollingDistance;
                Thread.sleep(waitTime);
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + length + ")");
            }
            //设置浏览窗口大小
            driver.manage().window().setSize(new Dimension(1440, height));
            // 重新拉到页面顶端
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(document.body.scrollHeight,0)");
            // 截图
            Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver);
            BufferedImage image = screenshot.getImage();
            ImageIO.write(image, "PNG", new File(fileName));
            stopWatch.stop();
            log.info("网站:{}", domain);
            webDriverManager.closeWebDriver(driver);
            log.info(stopWatch.prettyPrint());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        } finally {
//            webDriverManager.destroy();
        }
        return stopWatch.getTotalTimeSeconds();
    }
}
