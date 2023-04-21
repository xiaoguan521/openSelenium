package com.shineyue.selenium.utils;


import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author zhouzhiqiang
 * @version 1.0
 * @date 2022-09-23 15:34
 */

public class WebDriverPool implements IWebDriverPool {

    private static final Logger logger = LoggerFactory.getLogger(WebDriverPool.class);

    private WebDriverBean webDriverBean = null;


    /**
     * 驱动池可用状态
     */
    private Boolean isActive = true;

    /**
     * 空闲驱动池，由于读写操作较多，所以使用linklist
     */
    private LinkedList<WebDriver> freeWebDriver = new LinkedList<>();
    /**
     * 活动驱动池，由于读写操作较多，所以使用linklist
     */
    private LinkedList<WebDriver> activeWebDriver = new LinkedList<>();

    /**
     * 当前线程获得的连接
     */
    private ThreadLocal<WebDriver> currentWebDriver = new ThreadLocal<>();

    private WebDriverPool() {
        super();
    }

    public static WebDriverPool createWebDriverPool(WebDriverBean webDriverBean) {
        WebDriverPool webDriverPool = new WebDriverPool();
        webDriverPool.webDriverBean = webDriverBean;
        for (int i = 0; i < webDriverPool.webDriverBean.getInitConnections(); i++) {
            try {
                //获取web驱动
                WebDriver driver = webDriverPool.getInitWebDriver(webDriverBean);
                webDriverPool.freeWebDriver.add(driver);
            } catch (Exception e) {
                logger.error("驱动池初始化失败" + e.getMessage());
                return null;
            }
        }
        webDriverPool.isActive = true;
        return webDriverPool;
    }

    /**
     * 获取web驱动
     *
     * @return
     */
    private WebDriver getInitWebDriver(WebDriverBean webDriverBean) {
//        if (this.webDriverBean.getDriverPath() != null && this.webDriverBean.getDriverPath().length() > 1) {
//            System.setProperty("webdriver.chrome.driver", this.webDriverBean.getDriverPath());
//        }
//        ChromeOptions chromeOptions = getChromeOptions(this.webDriverBean);
//        WebDriver driver = new ChromeDriver(chromeOptions);
//        //浏览器最大化
//        driver.manage().window().maximize();
//        driver.get("https://www.sto.cn/Service/CustomerService?active_li=2&active_span=21");
        WebDriver driver = createWebDriver(webDriverBean);
        return driver;
    }

    /**
     * 检查驱动是否存活
     *
     * @param webDriver
     * @return
     */
    private Boolean isValidWebDriver(WebDriver webDriver) {

        try {
            if (webDriver == null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private WebDriver newWebDriver() {
        WebDriver webDriver = null;
        try {
            if (this.webDriverBean != null) {
                //获取web驱动
                webDriver = getInitWebDriver(this.webDriverBean);
            }
        } catch (Exception e) {
            logger.error("创建新的驱动失败");
        }
        return webDriver;
    }

    @Override
    public synchronized WebDriver getWebDriver() {
        WebDriver webDriver = null;
        if (this.getActiveNum() < this.webDriverBean.getMaxConnections()) {
            if (this.getFreeNum() > 0) {
                logger.info("空闲池中剩余驱动数为" + this.getFreeNum() + "，直接获取驱动");
                webDriver = this.freeWebDriver.pollFirst();
                if(webDriver!=null){
                    this.activeWebDriver.add(webDriver);
                }
            } else {
                logger.info("空闲池中无驱动，创建新的驱动");
                try {
                    webDriver = this.newWebDriver();
                    if(webDriver!=null){
                        this.activeWebDriver.add(webDriver);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            logger.info("当前已达最大驱动数");
            return null;

        }
        return webDriver;
    }

    @Override
    public synchronized WebDriver getCurrentConnecton() {
        WebDriver webDriver = this.currentWebDriver.get();
        try {
            if (!isValidWebDriver(webDriver)) {
                webDriver = this.getWebDriver();
            }
        } catch (Exception e) {
            logger.error("获取当前驱动失败" + e.getMessage());
        }
        return webDriver;
    }

    @Override
    public synchronized void releaseWebDriver(WebDriver driver) {
        logger.info(Thread.currentThread().getName() + "关闭连接:activeWebDriver.remove :" + driver);
        this.activeWebDriver.remove(driver);
        this.currentWebDriver.remove();
        try {
            if (isValidWebDriver(driver)) {
                driver.quit();
//                freeWebDriver.add(driver);
            } else {
                freeWebDriver.add(this.newWebDriver());
            }
        } catch (Exception e) {
            logger.error("释放当前驱动失败" + e.getMessage());
        }
        this.notifyAll();
    }

    @Override
    public synchronized void destroy() {
        for (WebDriver webDriver : this.freeWebDriver) {
            logger.info("空闲连接数：{}",this.freeWebDriver.size());
            try {
                if (isValidWebDriver(webDriver)) {
                    logger.info("进入销毁了吗：{}",true);
                    webDriver.quit();
                    this.freeWebDriver.removeFirst();
                    logger.info("能正11确执行吗：{}",true);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                logger.info("进入最终销毁了吗：{}",true);
                webDriver.quit();
                logger.info("能正确执行吗：{}",true);
            }
        }
        for (WebDriver webDriver : this.activeWebDriver) {
            logger.info("活动连接数：{}",this.activeWebDriver.size());
            try {
                if (isValidWebDriver(webDriver)) {
                    webDriver.quit();
                    this.activeWebDriver.removeFirst();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                webDriver.quit();
            }
        }
        logger.info("仍然在连接：{}",this.freeWebDriver.size()+this.activeWebDriver.size());
        this.isActive = false;
        this.freeWebDriver.clear();
        this.activeWebDriver.clear();
        logger.info("驱动池已经摧毁");
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    @Override
    public void checkPool() {
        ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(2);
        ses.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("空闲驱动数" + getFreeNum());
                logger.info("活动驱动数" + getActiveNum());
            }
        }, 1, 30, TimeUnit.SECONDS);
        ses.scheduleAtFixedRate(new checkFreepools(this), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public int getActiveNum() {
        return this.activeWebDriver.size();
    }

    @Override
    public int getFreeNum() {
        return this.freeWebDriver.size();
    }


    /**
     * 驱动池内部要保证指定最小数量的驱动数
     */
    class checkFreepools extends TimerTask {
        private WebDriverPool webDriverPool = null;

        public checkFreepools(WebDriverPool wp) {
            this.webDriverPool = wp;
        }

        @Override
        public void run() {
            if (this.webDriverPool != null && this.webDriverPool.isActive()) {
                int poolstotalnum = webDriverPool.getFreeNum()
                        + webDriverPool.getActiveNum();
                int subnum = webDriverPool.webDriverBean.getMinConnections()
                        - poolstotalnum;

                if (subnum > 0) {
                    logger.info("扫描并维持空闲池中的最小驱动数，需补充" + subnum + "个驱动");
                    for (int i = 0; i < subnum; i++) {
                        try {
                            webDriverPool.freeWebDriver
                                    .add(webDriverPool.newWebDriver());
                        } catch (Exception e) {
                            logger.error("补充驱动失败" + e.getMessage());
                        }
                    }
                }
            }
        }

    }

    /**
     * 获取浏览器配置
     *
     * @return
     */
    private static ChromeOptions getChromeOptions() {
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

        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings", 2);
        options.setExperimentalOption("prefs", prefs);
//        options.addArguments("blink-settings=imagesEnabled=false");//禁用图片

        //设置代理
//        String proxyIP = proxyUtil.getProxyIP(true);
//        if (StringUtils.isNotEmpty(proxyIP)) {
//            Proxy proxy = new Proxy().setHttpProxy(proxyIP).setSslProxy(proxyIP);
//            options.setProxy(proxy);
//        }

        return options;
    }


    /*创建去掉*/
    public WebDriver createWebDriver(WebDriverBean webDriverBean) {
        WebDriver driver = null;
        ChromeOptions options = getChromeOptions();
//        DesiredCapabilities capability = new DesiredCapabilities();
//        capability.setCapability(ChromeOptions.CAPABILITY, options);
//        capability.setBrowserName("chrome");
//        capability.setPlatform(Platform.LINUX);
//        options.merge(capability);
        try {
            logger.info("selenium 连接地址为：{}", webDriverBean.getRemoteAddr());
            driver = new RemoteWebDriver(new URL(webDriverBean.getRemoteAddr()), options);
            logger.info("sdk 完成");
        } catch (MalformedURLException e) {
            logger.error("driver异常：{}", e);
        }
        return driver;
    }
}
