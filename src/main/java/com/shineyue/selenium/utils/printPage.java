package com.shineyue.selenium.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Base64;

/**
 * Author liuXiaoChen
 * Date 2022-12-20 - 15:37
 * Description TODO
 */
public class printPage {
//    public static void main(String[] args) throws IOException {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--disable-logging");
//        // 字符编码 utf-8 支持中文字符
//        options.addArguments("lang=zh_CN.UTF-8");
//        // 设置容许弹框
//        options.addArguments("disable-infobars", "disable-web-security");
//        // 驱动自动控制软件标识
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
//        // 设置无gui 开发时仍是不要加，能够看到浏览器效果
//        options.addArguments("--headless");
//        options.addArguments("--window-size=1920,1080");
//        options.addArguments("--ignore-certificate-errors");
//        options.addArguments("--disable-gpu");//禁止gpu渲染
//        options.addArguments("--no-sandbox");//关闭沙盒模式
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--incognito"); // 隐身模式（无痕模式）
//        options.addArguments("--disable-extensions"); // disabling extensions
//
//        //禁用日志
//        options.addArguments("--log-level=3");
//        options.addArguments("--silent");
//        WebDriver webDriver = new  RemoteWebDriver(new URL("http://192.168.56.10:4444/wd/hub"),options);
//        webDriver.get("https://bbs.huaweicloud.com/blogs/344778");
//        PrintsPage printer = (PrintsPage) webDriver;
//
//        PrintOptions printOptions = new PrintOptions();
//        printOptions.setPageRanges("1-2");
//        String path = Utils.getPath("url2pdf", ".pdf");
//
//        Pdf pdf = printer.print(printOptions);
//
//        String content = pdf.getContent();
//        Files.write(Paths.get(path), Base64.getDecoder().decode(content), StandardOpenOption.CREATE);
//
//        System.out.println(path);
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-logging");
        // 字符编码 utf-8 支持中文字符
        options.addArguments("lang=zh_CN.UTF-8");
        // 设置容许弹框
        options.addArguments("disable-infobars", "disable-web-security");
        // 驱动自动控制软件标识
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        // 设置无gui 开发时仍是不要加，能够看到浏览器效果
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-gpu");//禁止gpu渲染
        options.addArguments("--no-sandbox");//关闭沙盒模式
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--incognito"); // 隐身模式（无痕模式）
        options.addArguments("--disable-extensions"); // disabling extensions

        //禁用日志
        options.addArguments("--log-level=3");
        options.addArguments("--silent");
        WebDriver webDriver = new  RemoteWebDriver(new URL("http://192.168.56.10:4444/wd/hub"),options);
        webDriver.get("https://bbs.huaweicloud.com/blogs/344778");
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
//        Thread.sleep(3000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("cloud-blog-detail-left")));
        WebElement element = webDriver.findElement(By.className("cloud-blog-detail-left"));
        File scrFile = element.getScreenshotAs(OutputType.FILE);
//        File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        String path = Utils.getPath("url13123image", ".png");
        FileUtils.copyFile(scrFile, new File(path));
        webDriver.quit();
    }
}
