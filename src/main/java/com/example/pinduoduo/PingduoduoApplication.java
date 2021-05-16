package com.example.pinduoduo;

import com.bstek.ureport.console.UReportServlet;
import com.example.pinduoduo.utils.SpringHolder;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ImportResource("classpath:ureport-console-context.xml")
@SpringBootApplication
public class PingduoduoApplication implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/doc.html");
    }

    @Bean
    public ServletRegistrationBean ureportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
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
