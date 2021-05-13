package com.example.pinduoduo.selenium;

import com.example.pinduoduo.selenium.handlers.LoginMmsHandler;
import com.example.pinduoduo.selenium.handlers.ShowDeliveredPhoneHandler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class CustomerInfoEmulator {

    public void simulation() {
        System.setProperty("webdriver.chrome.driver", "driver/mac/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-blink-features=AutomationControlled");
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy("127.0.0.1:24000");
//        options.setProxy(proxy);
        WebDriver driver = new ChromeDriver(options);

        log.info("开始获取订单信息");
        HandlerChain handlerChain = new HandlerChain();
        handlerChain.addHandler(new LoginMmsHandler());
        handlerChain.addHandler(new ShowDeliveredPhoneHandler());
        handlerChain.doHandler(driver);
    }
}
