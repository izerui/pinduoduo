package com.example.pinduoduo;

import com.example.pinduoduo.utils.SpringHolder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PingduoduoApplication implements WebMvcConfigurer, CommandLineRunner {

    @Autowired
    private ServerProperties serverProperties;

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

    @Override
    public void run(String... args) throws Exception {
        List<String> commands = new ArrayList<>();
        commands.add("/usr/local/bin/node");
        commands.add("-e");
        commands.add("const open = require('/Users/serv/dev/github/pinduoduo/open.js').open;open('http://localhost:" + serverProperties.getPort() + "/');");
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.start().waitFor();
    }
}
