package com.example.pinduoduo.controller;

import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.selenium.CustomerInfoEmulator;
import com.example.pinduoduo.service.OrderService;
import com.example.pinduoduo.support.Description;
import com.example.pinduoduo.utils.ExcelMapper;
import com.example.pinduoduo.utils.MapExporter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "导出拼多多订单信息")
@RestController
public class PddController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("导出订单数据")
    @GetMapping("/export/{startDate}")
    public ResponseEntity<byte[]> exportOrderInfoList(@PathVariable("startDate") String startDate) throws Exception {
        DateTime dateTime = DateTime.parse(startDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
        List<OrderInfo> orderInfos = orderService.findVaildBySendDate(dateTime.withTimeAtStartOfDay().toDate());
        List<LinkedHashMap<String, String>> collect = orderInfos.stream().map(orderInfo -> {
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            for (Field field : orderInfo.getClass().getDeclaredFields()) {
                Description description = field.getDeclaredAnnotation(Description.class);
                if (description != null) {
                    String value = description.value();
                    field.setAccessible(true);
                    linkedHashMap.put(value, String.valueOf(ReflectionUtils.getField(field, orderInfo)));
                }
            }
            return linkedHashMap;
        }).collect(Collectors.toList());
        MapExporter export = new MapExporter("订单信息", collect);
        SXSSFWorkbook workbook = export.writeBook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] bytes = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", startDate + ".xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }


    @ApiOperation("根据sql导出数据")
    @GetMapping("/export/sql")
    public ResponseEntity<byte[]> exportBySQL(String sql) throws Exception {
        List<Map<String, Object>> list = orderService.findMapBySQL(sql);
        List<LinkedHashMap<String, String>> collect = list.stream().map(stringObjectMap -> {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            stringObjectMap.forEach((o, o2) -> {
                map.put(o, String.valueOf(o2));
            });
            return map;
        }).collect(Collectors.toList());
        MapExporter export = new MapExporter("订单信息", collect);
        SXSSFWorkbook workbook = export.writeBook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] bytes = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "order_info_sql.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }


    @ApiOperation("导出重复数据")
    @GetMapping("/export/repeat")
    public ResponseEntity<byte[]> exportRepeat() throws Exception {
        String sql = "SELECT receiver '收件人',phone '手机号',city '省市区',address '地址',count(0) '购买次数',SUM(pay_amount) '合计支付金额' from order_info where receiver <> '**' GROUP BY receiver HAVING count(0) > 1";
        return exportBySQL(sql);
    }


    @ApiOperation("抓取订单信息")
    @GetMapping("/fetch")
    public String fetchOrderInfos() {
        new Thread(() -> {
            new CustomerInfoEmulator().simulation();
        }).start();
        return "开始抓取...";
    }


    @ApiOperation("导入历史订单信息")
    @PostMapping("/import-history")
    public String importHistoryExcel(@RequestParam("file") @RequestPart MultipartFile file) throws Exception {
        ExcelMapper mapper = new ExcelMapper("订单信息.xlsx", file.getBytes());
        List<Map<String, String>> dataList = mapper.read(null, rowData -> rowData);
        int count = orderService.importHistory(dataList);
        return "成功导入... " + count + "条";
    }
}
