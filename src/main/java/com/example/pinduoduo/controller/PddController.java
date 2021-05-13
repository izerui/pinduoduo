package com.example.pinduoduo.controller;

import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.service.OrderService;
import com.example.pinduoduo.support.Description;
import com.example.pinduoduo.utils.MapExporter;
import io.swagger.annotations.Api;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "导出拼多多订单信息")
@RestController
public class PddController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportOrderInfoList() throws Exception {
        List<OrderInfo> orderInfos = orderService.listOrderInfos();
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
        headers.setContentDispositionFormData("attachment", new String("订单信息".getBytes(StandardCharsets.UTF_8), "ISO-8859-1") + ".xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }

}
