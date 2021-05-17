package com.example.pinduoduo.ureport;

import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReportDataSourceService {

    @Autowired
    private OrderService orderService;

    public List<OrderInfo> getOrderDataSource(String dataSourceName, String dataSetName, Map<String, Object> params) {
        return orderService.selectOrderInfoReports(params);
    }
}
