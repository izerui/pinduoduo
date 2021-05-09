package com.example.pingduoduo.selenium;

import org.openqa.selenium.WebDriver;

public interface Handler {
    void doHandler(WebDriver driver, HandlerChain handlerChain) throws Exception;
}
