package com.example.pinduoduo.ureport;

import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@ImportResource("classpath:ureport-console-context.xml")
@Configuration
public class UreportConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public ServletRegistrationBean ureportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }

    @Bean
    public BuildinDatasource ureportDatasource() {
        return new BuildinDatasource() {
            @Override
            public String name() {
                return "ureportDataSource";
            }

            @Override
            public Connection getConnection() {
                return DataSourceUtils.getConnection(dataSource);
            }
        };
    }

}
