package com.example.pingduoduo.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HandlerChain {

    private List<Handler> handlers = new ArrayList<>();
    private int index = 0;

    public HandlerChain addHandler(Handler handler) {
        handlers.add(handler);
        return this;
    }

    public void doHandler(WebDriver driver) {
        log.info("--------------- handler start ---------------");
        if (index == handlers.size()) {
            return;
        }
        Handler next = handlers.get(index);
        index++;
        try {
            next.doHandler(driver, this);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }finally {
            driver.quit();
        }
        log.info("--------------- handler end ---------------");
    }
}
