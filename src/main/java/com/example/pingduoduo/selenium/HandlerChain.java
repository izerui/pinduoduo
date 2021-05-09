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
        if (index == handlers.size()) {
            return;
        }
        Handler next = handlers.get(index);
        index++;
        try {
            log.info("handler:: " + next.getClass().getName());
            next.doHandler(driver, this);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            driver.quit();
        }
    }
}
