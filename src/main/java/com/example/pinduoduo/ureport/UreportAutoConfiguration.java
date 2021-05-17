package com.example.pinduoduo.ureport;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:ureport-console-context.xml")
@Configuration
public class UreportAutoConfiguration {

    @Bean
    public ServletRegistrationBean ureportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }

}
