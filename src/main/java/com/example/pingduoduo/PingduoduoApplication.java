package com.example.pingduoduo;

import com.example.pingduoduo.selenium.CustomerInfoEmulator;
import com.google.gson.Gson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PingduoduoApplication implements WebMvcConfigurer, CommandLineRunner {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/");
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    public static void main(String[] args) {
        SpringApplication.run(PingduoduoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new CustomerInfoEmulator().simulation();
    }
}
