package com.example.pinduoduo.service;

import com.example.pinduoduo.dao.OrderInfoDao;
import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.support.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderInfoDao orderInfoDao;


    public List<OrderInfo> findEffectives() {
        return orderInfoDao.findEffectives();
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

    public int importHistory(List<Map<String, String>> dataList) {
        int count = 0;
        for (Map<String, String> map : dataList) {
            OrderInfo orderInfo = new OrderInfo();
            map.forEach((key, value) -> {
                Field[] fields = OrderInfo.class.getDeclaredFields();
                for (Field field : fields) {
                    Description annotation = field.getAnnotation(Description.class);
                    if (annotation != null && annotation.value().equals(key)) {
                        field.setAccessible(true);
                        try {
                            if (BigDecimal.class.equals(field.getType())) {
                                field.set(orderInfo, new BigDecimal(value));
                            } else if (Integer.class.equals(field.getType())) {
                                field.set(orderInfo, Integer.valueOf(value));
                            } else {
                                field.set(orderInfo, value);
                            }
                        } catch (IllegalAccessException e) {
                            log.error(e.getMessage(), e);
                        }
                        break;
                    }
                }
            });
            if (!existsByOrderNo(orderInfo.getOrderNo())) {
                saveOrderInfo(orderInfo);
                count++;
            }
        }
        return count;
    }
}