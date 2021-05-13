package com.example.pinduoduo.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return SpringHolder.applicationContext;
    }

    public static <T> T getBean(Class<T> tClass) {
        return SpringHolder.applicationContext.getBean(tClass);
    }
}
