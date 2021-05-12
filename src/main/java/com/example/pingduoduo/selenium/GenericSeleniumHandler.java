package com.example.pingduoduo.selenium;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.retry.support.RetryTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

@Slf4j
public abstract class GenericSeleniumHandler implements Handler {

    private WebDriver webDriver;

    @Override
    public final void doHandler(WebDriver driver, HandlerChain handlerChain) throws Exception {
        this.webDriver = driver;
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
                log.error(ex.getMessage());
                throw ex;
            }
            return null;
        });
    }

    protected <V> V waitUtilThrow(int waitSeconds, boolean timeoutThrow, Callable<V> callable) {
        return waitUtil(10, true, callable);
    }

    protected <V> V waitUtil(Callable<V> callable) {
        return waitUtil(10, false, callable);
    }

    protected <V> V waitUtil(int waitSeconds, boolean timeoutThrow, Callable<V> callable) {
        Clock clock = Clock.systemDefaultZone();
        Duration timeout = Duration.ofSeconds(waitSeconds);
        Duration interval = Duration.ofMillis(500);
        Instant endTime = clock.instant().plus(timeout);
        Throwable lastException = null;
        while (true) {
            try {
                V value = callable.call();
                if (value != null && (Boolean.class != value.getClass() || Boolean.TRUE.equals(value))) {
                    return value;
                }
            } catch (Exception e) {
                lastException = e;
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
        String timeoutMessage = String.format("Expected condition failed: (tried for %d second(s) with %d milliseconds interval)", timeout.getSeconds(), interval.toMillis());
        log.error(timeoutMessage);
        if (timeoutThrow) {
            throw new RuntimeException(timeoutMessage, lastException);
        } else {
            return null;
        }
    }

    protected void sleepSeconds(int start, int end) throws InterruptedException {
        Thread.sleep(RandomUtils.nextInt(start * 1000, end * 1000));
    }

    public interface RetryCall {
        void doWithRetry() throws Exception;
    }
}
