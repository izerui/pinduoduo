package com.example.pinduoduo.ureport;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.example.pinduoduo.entity.Report;
import com.example.pinduoduo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DbReportProvider implements ReportProvider {

    @Autowired
    private ReportService reportService;

    @Override
    public InputStream loadReport(String name) {
        Report report = reportService.getReport(getReportName(name));
        if (report == null) {
            return null;
        }
        return new ByteArrayInputStream(report.getBytes());
    }

    @Override
    public void deleteReport(String name) {
        reportService.deleteReport(getReportName(name));
    }

    @Override
    public List<ReportFile> getReportFiles() {
        List<Report> reports = reportService.findReports();
        return reports.stream().map(report -> new ReportFile(getReportName(report.getName()), report.getUpdateDate())).collect(Collectors.toList());
    }

    @Override
    public void saveReport(String name, String content) {
        String reportName = getReportName(name);
        Report report = reportService.getReport(reportName);
        if (report == null) {
            report = new Report();
            report.setName(reportName);
        }
        try {
            report.setUpdateDate(new Date());
            report.setBytes(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        reportService.saveReport(report);
    }

    @Override
    public String getName() {
        return "数据库系统";
    }

    @Override
    public boolean disabled() {
        return false;
    }

    @Override
    public String getPrefix() {
        return "db:";
    }

    private String getReportName(String name) {
        if (name.startsWith(this.getPrefix())) {
            name = name.substring(this.getPrefix().length(), name.length());
        }
        return name;
    }
}
