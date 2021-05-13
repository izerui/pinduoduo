package com.example.pinduoduo.dao;

import com.example.pinduoduo.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInfoDao extends JpaRepository<OrderInfo, String> {
}
