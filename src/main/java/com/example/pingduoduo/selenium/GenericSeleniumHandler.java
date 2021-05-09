package com.example.pingduoduo.selenium;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.function.Function;

public abstract class GenericSeleniumHandler implements Handler {

    private WebDriver webDriver;

    @Override
    public final void doHandler(WebDriver webDriver, HandlerChain handlerChain) throws Exception {
        this.webDriver = webDriver;
        this.doHandlerInternal(webDriver, handlerChain);
        handlerChain.doHandler(webDriver);
    }

    protected abstract void doHandlerInternal(WebDriver webDriver, HandlerChain handlerChain) throws Exception;

    protected <T, E extends Throwable> T retry(int attempts,int interval, RetryCallback<T, E> retryCallback) throws E {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(attempts)
                .fixedBackoff(interval)
                .retryOn(Exception.class)
                .build();
        return retryTemplate.execute(retryCallback);
    }

    protected <V> V waitUtil(Function<? super WebDriver, V> isTrue) {
        return new WebDriverWait(webDriver, 10L).until(isTrue);
    }

    protected void sleepSeconds(int start, int end) throws InterruptedException {
        Thread.sleep(RandomUtils.nextInt(start * 1000, end * 1000));
    }
}
