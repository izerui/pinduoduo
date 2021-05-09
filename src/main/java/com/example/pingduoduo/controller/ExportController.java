package com.example.pingduoduo.controller;

import com.example.pingduoduo.utils.MapExporter;
import com.example.pingduoduo.utils.NutchUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "导出拼多多订单信息")
@RestController
public class ExportController {

    @Autowired
    private Gson gson;

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportOrderInfoList(@RequestParam("anti") String anti,
                                                      @RequestParam("cookie") String cookie) throws Exception {

        String body = "{\"orderType\":2,\"afterSaleType\":1,\"remarkStatus\":-1,\"urgeShippingStatus\":-1,\"groupStartTime\":1617526115,\"groupEndTime\":1620118115,\"pageNumber\":1,\"pageSize\":20,\"sortType\":11}";
        String result = NutchUtils.postJson("https://mms.pinduoduo.com/mangkhut/mms/recentOrderList", body, anti, cookie);

        Map map = gson.fromJson(result, Map.class);
        if (MapUtils.getBoolean(map, "success")) {
            Map data = MapUtils.getMap(map, "result");
            Integer totalItemNum = MapUtils.getInteger(data, "totalItemNum");
            if (totalItemNum != null && totalItemNum > 0) {
                List<LinkedTreeMap> list = (List<LinkedTreeMap>) data.get("pageItems");
                List collect = list.stream().map(o -> {
                    LinkedHashMap r = new LinkedHashMap();
                    r.put("商品名称",String.valueOf(o.get("goods_name")));
                    r.put("订单状态",String.valueOf(o.get("order_status_str")));
                    r.put("商品数量",String.valueOf(o.get("goods_number")));
                    r.put("商品总价",String.valueOf(MapUtils.getDoubleValue(o,"goods_amount") / 100));
                    r.put("实收金额",String.valueOf(MapUtils.getDoubleValue(o,"order_amount") / 100));
//                    r.put("成交时间",new DateTime(new Date(MapUtils.getLongValue(o,"order_time") * 1000)).toString("yyyy-MM-dd HH:mm:ss"));
//                    r.put("承诺发货时间",new DateTime(new Date(MapUtils.getLongValue(o,"promise_shipping_time") * 1000)).toString("yyyy-MM-dd HH:mm:ss"));
                    return r;
                }).collect(Collectors.toList());
                String batchNo = "订单信息";
                MapExporter export = new MapExporter(batchNo, collect);
                SXSSFWorkbook workbook = export.writeBook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                workbook.close();

                byte[] bytes = outputStream.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("attachment", batchNo + ".xlsx");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(bytes.length);
                return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
            }
            throw new RuntimeException("没有数据");
        }
        throw new RuntimeException("请求出错: " + MapUtils.getString(map, "errorMsg"));
    }
}
