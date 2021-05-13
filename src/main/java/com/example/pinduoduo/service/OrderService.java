package com.example.pinduoduo.service;

import com.example.pinduoduo.dao.OrderInfoDao;
import com.example.pinduoduo.entity.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderInfoDao orderInfoDao;


    public List<OrderInfo> listOrderInfos() {
        return orderInfoDao.findAll();
    }

    public boolean existsByOrderNo(String orderNo) {
        return orderInfoDao.existsById(orderNo);
    }

    public void saveOrderInfo(OrderInfo orderInfo) {
        if (!orderInfoDao.existsById(orderInfo.getOrderNo())) {
            orderInfoDao.save(orderInfo);
        } else {
            log.warn("订单号[{}]已存在: ", orderInfo.getOrderNo());
        }
    }
}
