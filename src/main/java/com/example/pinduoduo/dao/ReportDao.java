package com.example.pinduoduo.dao;

import com.example.pinduoduo.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportDao extends JpaRepository<Report, String> {
}
