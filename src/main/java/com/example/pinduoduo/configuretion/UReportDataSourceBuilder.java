package com.example.pinduoduo.configuretion;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class UReportDataSourceBuilder implements BuildinDatasource {
    @Autowired
    private DataSource dataSource;


    @Override
    public String name() {
        return "ureportDataSource";
    }

    @Override
    public Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
}
