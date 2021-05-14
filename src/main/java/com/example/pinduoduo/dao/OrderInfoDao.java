package com.example.pinduoduo.dao;

import com.example.pinduoduo.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderInfoDao extends JpaRepository<OrderInfo, String> {

    @Query("select x from OrderInfo x where x.sendTime >= ?1 and x.receiver <> '**'")
    List<OrderInfo> findVaildBySendDate(Date startDate);
}
