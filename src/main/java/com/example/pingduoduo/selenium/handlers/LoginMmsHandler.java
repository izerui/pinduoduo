package com.example.pingduoduo.selenium.handlers;

import com.example.pingduoduo.selenium.GenericSeleniumHandler;
import com.example.pingduoduo.selenium.HandlerChain;
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
        driver.get("https://mms.pinduoduo.com/login");

        // 切换到账户登录
        findElement(driver, By.cssSelector("div[class='tab-item last-item']")).click();

        // 读取用户名密码
        List<String> userPass = Files.readAllLines(Paths.get("passwd"));

        // 输入账号密码
        findElement(driver, By.id("usernameId")).sendKeys(userPass.get(0));
        findElement(driver, By.id("passwordId")).sendKeys(userPass.get(1));

        sleepSeconds(0, 3);
        // 提交登录
        findElement(driver, By.tagName("button")).click();

        // 是否已经登录到home页
        boolean homePage = waitUtil(() -> driver.getCurrentUrl()).equals("https://mms.pinduoduo.com/home");
        // 如果还是停留在登陆页,就需要验证手机验证码
        if (!homePage) {
            WebElement webElement = findElement(driver, By.cssSelector("div[data-testid='beast-core-input-suffix']"));
            if (webElement != null) {
                WebElement element = findElement(webElement, By.xpath("div/a/span"));
                if (element != null) {
                    element.click();
                    Thread.sleep(60000);
                }
            }
        }
    }
}
