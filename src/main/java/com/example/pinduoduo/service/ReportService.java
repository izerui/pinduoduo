package com.example.pinduoduo.service;

import com.example.pinduoduo.dao.ReportDao;
import com.example.pinduoduo.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReportService {

    @Autowired
    private ReportDao reportDao;

    public Report getReport(String name) {
        Optional<Report> byId = reportDao.findById(name);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            return null;
        }
    }

    public void deleteReport(String name) {
        reportDao.deleteById(name);
    }

    public List<Report> findReports() {
        return reportDao.findAll();
    }

    public void saveReport(Report report) {
        reportDao.save(report);
    }


}
