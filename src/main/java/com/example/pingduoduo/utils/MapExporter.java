package com.example.pingduoduo.utils;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MapExporter {

    private final String sheetName;
    private List<LinkedHashMap<String, String>> dataList;
    private SXSSFWorkbook wb;
    private SXSSFSheet sh;
    private CellStyle _headerStyle;
    private CellStyle _dataStyle;

    public MapExporter(String sheetName, List<LinkedHashMap<String, String>> dataList) {
        this.dataList = dataList;
        this.sheetName = sheetName;
        this.init();
    }

    private void init() {

        wb = new SXSSFWorkbook();

        sh = wb.createSheet(sheetName);
        sh.trackAllColumnsForAutoSizing();

        Font headFont = wb.createFont();
//        headFont.setBold(true);
        headFont.setFontHeightInPoints((short) 14);
        _headerStyle = wb.createCellStyle();
        _headerStyle.setBorderBottom(BorderStyle.THIN);
        _headerStyle.setBorderTop(BorderStyle.THIN);
        _headerStyle.setBorderLeft(BorderStyle.THIN);
        _headerStyle.setBorderRight(BorderStyle.THIN);
        _headerStyle.setFont(headFont);
        _headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        _headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 12);

        _dataStyle = wb.createCellStyle();
        _dataStyle.setBorderBottom(BorderStyle.THIN);
        _dataStyle.setBorderTop(BorderStyle.THIN);
        _dataStyle.setBorderLeft(BorderStyle.THIN);
        _dataStyle.setBorderRight(BorderStyle.THIN);
        _dataStyle.setFont(cellFont);
    }


    public SXSSFWorkbook writeBook() {
        //header
        Assert.notEmpty(dataList);
        Row headRow = sh.createRow(0);
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sh.createRow(i + 1);
            row.setHeightInPoints((short) 25);
            LinkedHashMap<String, String> map = dataList.get(i);
            AtomicInteger j = new AtomicInteger();
            int finalI = i;
            map.forEach((key, value) -> {
                //标题行
                if (finalI == 0) {
                    //序号
                    if (j.get() == 0) {
                        createCell(headRow, j.get(), _headerStyle, "序号");
                    }
                    //其他map的key
                    createCell(headRow, j.get() + 1, _headerStyle, key);
                }
                //序号列
                if (j.get() == 0) {
                    createCell(row, j.get(), _dataStyle, finalI + 1 + "");
                }
                //数据列
                createCell(row, j.get() + 1, _dataStyle, value);
                j.getAndIncrement();
            });
        }
        return wb;
    }

    private void createCell(Row row, int column, CellStyle cellStyle, String cellValue) {
        Cell cell = row.createCell(column, CellType.STRING);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue == null ? "" : cellValue);
    }


//    public static void main(String[] args) throws IOException {
//        List<LinkedHashMap<String, String>> datalist = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            LinkedHashMap<String, String> map = new LinkedHashMap<>();
//            for (int j = 0; j < 10; j++) {
//                map.put("key" + j, "value" + j + "-" + i);
//            }
//            System.out.println(map);
//            datalist.add(map);
//        }
//        System.out.println(datalist);
//        MapExport mapExport = new MapExport("导出结果", datalist);
//        SXSSFWorkbook workbook = mapExport.writeBook();
//        workbook.write(new FileOutputStream("/www/mapExport.xlsx"));
//
////        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////        workbook.write(outputStream);
//        workbook.close();
//
//    }

}
