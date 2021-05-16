package com.example.pinduoduo.dao;

import com.example.pinduoduo.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderInfoDao extends JpaRepository<OrderInfo, String> {

    @Query("select x from OrderInfo x where x.sendTime >= ?1 and x.receiver <> '**'")
    List<OrderInfo> findVaildBySendDate(Date startDate);

    @Query("select x from OrderInfo x where x.receiver like :receiver and x.sendDate = :sendDate")
    List<OrderInfo> findOrders(@Param("receiver") String receiver, @Param("sendDate") String sendDate);

    @Query("select x from OrderInfo x where x.receiver like :receiver")
    List<OrderInfo> findByReceiver(@Param("receiver") String receiver);

    List<OrderInfo> findBySendDate(String sendDate);

    @Query("select x from OrderInfo x where x.receiver <> '**'")
    List<OrderInfo> findByVaild();

}
