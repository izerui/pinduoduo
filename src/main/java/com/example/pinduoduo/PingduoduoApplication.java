package com.example.pinduoduo;

import com.example.pinduoduo.utils.SpringHolder;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PingduoduoApplication implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/");
    }

    @Bean
    @Order(0)
    public SpringHolder springHolder() {
        return new SpringHolder();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    public static void main(String[] args) {
        SpringApplication.run(PingduoduoApplication.class, args);
    }

}
