package com.example.pinduoduo.selenium;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public abstract class GenericSeleniumHandler implements Handler {

    @Override
    public final void doHandler(WebDriver driver, HandlerChain handlerChain) throws Exception {
        this.doHandlerInternal(driver, handlerChain);
        handlerChain.doHandler(driver);
    }

    protected abstract void doHandlerInternal(WebDriver driver, HandlerChain handlerChain) throws Exception;

    protected void retry(int attempts, int interval, RetryCall retryCall) throws Exception {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(attempts)
                .fixedBackoff(interval)
                .retryOn(Exception.class)
                .build();
        retryTemplate.<Void, Exception>execute(retryContext -> {
            try {
                retryCall.doWithRetry();
            } catch (Exception ex) {
                log.error(getThrowDetailMessage(ex), ex);
                throw ex;
            }
            return null;
        });
    }

    protected void waitUtilGet(WebDriver driver, String url) {
        driver.get(url);
        waitUtil(() -> {
            Assert.state(driver.getCurrentUrl().equals(url), "正在加载页面...");
            return driver.getCurrentUrl();
        });
    }

    protected WebElement waitUtilElement(WebDriver driver, By by) {
        return waitUtil(() -> driver.findElement(by));
    }

    protected WebElement waitUtilElement(WebDriver driver, By by, int waitSeconds) {
        return waitUtil(waitSeconds, () -> driver.findElement(by));
    }

    protected WebElement waitUtilElement(WebElement element, By by) {
        return waitUtil(() -> element.findElement(by));
    }

    protected WebElement waitUtilElement(WebElement element, By by, int waitSeconds) {
        return waitUtil(waitSeconds, () -> element.findElement(by));
    }

    protected List<WebElement> waitUtilElements(WebDriver driver, By by) {
        return waitUtil(() -> driver.findElements(by));
    }

    protected List<WebElement> waitUtilElements(WebDriver driver, By by, int waitSeconds) {
        return waitUtil(waitSeconds, () -> driver.findElements(by));
    }

    protected List<WebElement> waitUtilElements(WebElement element, By by) {
        return waitUtil(() -> element.findElements(by));
    }

    protected List<WebElement> waitUtilElements(WebElement element, By by, int waitSeconds) {
        return waitUtil(waitSeconds, () -> element.findElements(by));
    }

    protected <V> V waitUtil(Callable<V> callable) {
        return waitUtil(10, callable);
    }

    protected <V> V waitUtil(int waitSeconds, Callable<V> callable) {
        Clock clock = Clock.systemDefaultZone();
        Duration timeout = Duration.ofSeconds(waitSeconds);
        Duration interval = Duration.ofMillis(500);
        Instant endTime = clock.instant().plus(timeout);
        while (true) {
            try {
                V value = callable.call();
                if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
                    return value;
                }
            } catch (Exception e) {
            }

            if (endTime.isBefore(clock.instant())) {
                break;
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String getThrowDetailMessage(Throwable throwable) {
        try {
            Field detailMessage = ReflectionUtils.findField(throwable.getClass(), "detailMessage");
            if (detailMessage != null) {
                detailMessage.setAccessible(true);
                return (String) detailMessage.get(throwable);
            }
            return throwable.getMessage();
        } catch (Exception de) {
            return throwable.getMessage();
        }
    }

    protected void sleepSeconds(int start, int end) throws InterruptedException {
        Thread.sleep(RandomUtils.nextInt(start * 1000, end * 1000));
    }

    public interface RetryCall {
        void doWithRetry() throws Exception;
    }
}
