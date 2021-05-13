package com.example.pinduoduo.selenium.handlers;

import com.example.pinduoduo.selenium.GenericSeleniumHandler;
import com.example.pinduoduo.selenium.HandlerChain;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 登录拼多多商家后台
 */
public class LoginMmsHandler extends GenericSeleniumHandler {
    @Override
    protected void doHandlerInternal(WebDriver driver, HandlerChain handlerChain) throws Exception {
        // 打开登录页面
        waitUtilGet(driver, "https://mms.pinduoduo.com/login");

        // 切换到账户登录
        waitUtilElement(driver, By.cssSelector("div[class='tab-item last-item']")).click();

        // 读取用户名密码
        List<String> userPass = Files.readAllLines(Paths.get("passwd"));

        // 输入账号密码
        waitUtilElement(driver, By.id("usernameId")).sendKeys(userPass.get(0));
        waitUtilElement(driver, By.id("passwordId")).sendKeys(userPass.get(1));

        sleepSeconds(0, 3);
        // 提交登录
        waitUtilElement(driver, By.tagName("button")).click();

        // 是否已经登录到home页
        waitUtil(() -> {
            if (driver.getCurrentUrl().equals("https://mms.pinduoduo.com/home")) {
                return driver.getCurrentUrl();
            }
            // 如果还是停留在登陆页,就需要验证手机验证码
            WebElement webElement = waitUtilElement(driver, By.cssSelector("div[data-testid='beast-core-input-suffix']"), 3);
            if (webElement != null) {
                WebElement element = waitUtilElement(webElement, By.xpath("div/a/span"), 3);
                if (element != null) {
                    element.click();
                    Thread.sleep(60000);
                }
            }
            return null;
        });
    }
}
