package com.example.pingduoduo.selenium.handlers;

import com.example.pingduoduo.selenium.GenericSeleniumHandler;
import com.example.pingduoduo.selenium.HandlerChain;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 登录拼多多商家后台
 */
public class LoginMmsHandler extends GenericSeleniumHandler {
    @Override
    protected void doHandlerInternal(WebDriver webDriver, HandlerChain handlerChain) throws Exception {
        // 打开登录页面
        webDriver.get("https://mms.pinduoduo.com/login");

        // 切换到账户登录
        waitUtil(driver -> driver.findElement(By.cssSelector("div[class='tab-item last-item']"))).click();

        // 读取用户名密码
        List<String> userPass = Files.readAllLines(Paths.get("passwd"));

        // 输入账号密码
        webDriver.findElement(By.id("usernameId")).sendKeys(userPass.get(0));
        webDriver.findElement(By.id("passwordId")).sendKeys(userPass.get(1));

        sleepSeconds(0,3);
        // 提交登录
        webDriver.findElement(By.tagName("button")).click();
    }
}
