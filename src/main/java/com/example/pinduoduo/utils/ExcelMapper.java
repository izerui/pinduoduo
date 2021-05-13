package com.example.pinduoduo.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Created by serv on 15/9/8.
 */
public class ExcelMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelMapper.class);


    private String fileName;
    private byte[] data;
    private String sheetName;
    private int headerRowIndex = 0;

    private long _time = System.currentTimeMillis();

    /**
     * @param excelFile
     * @throws IOException
     */
    public ExcelMapper(File excelFile) throws IOException {
        if (excelFile.length() > 5 * 1024 * 1000) {
            throw new RuntimeException("excel文件超过5M大小,,请删除不必要的信息后重试");
        }
        this.fileName = excelFile.getName();
        this.data = FileUtils.readFileToByteArray(excelFile);
        System.out.println("读取文件耗时:" + (System.currentTimeMillis() - _time) / 1000);
    }

    /**
     * @param fileName exel表文件
     * @param data     exel表内容
     */
    public ExcelMapper(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    /**
     * 要读取的sheet名称
     *
     * @param sheetName
     * @return
     */
    public ExcelMapper withSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * head头所在的行索引 从0开始
     *
     * @param headerRowIndex
     * @return
     */
    public ExcelMapper withHeaderRowIndex(int headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
        return this;
    }

    /**
     * 开始读取excel文件内容到一个List<Map>
     *
     * @return
     * @throws IOException
     */
    public <T> List<T> read(HeaderFormatter formatter, Convertor<T> convertor) throws Exception {
        List<T> results = new ArrayList<T>();
        if (isExcel2003(fileName)) {
            results = getData(new HSSFWorkbook(new ByteArrayInputStream(data)), headerRowIndex, formatter, convertor);
        }
        if (isExcel2007(fileName)) {
            results = getData(new XSSFWorkbook(new ByteArrayInputStream(data)), headerRowIndex, formatter, convertor);
        }
        return results;
    }

    private <T> List<T> getData(Workbook book, int headerRowIndex, HeaderFormatter formatter, Convertor<T> convertor) throws Exception {
        System.out.println("准备getData耗时:" + (System.currentTimeMillis() - _time) / 1000);
        Sheet sheet = null;
        if (StringUtils.isNotEmpty(sheetName)) {
            sheet = book.getSheet(sheetName);
            Assert.state(sheet != null, "EXCEL表 " + sheetName + " 未找到");
        } else {

            sheet = book.getSheetAt(0);
            Assert.state(sheet != null, "EXCEL表未找到");
        }

        System.out.println("开始读取耗时:" + (System.currentTimeMillis() - _time) / 1000);

        Row headRow = sheet.getRow(headerRowIndex);

        List<T> dataList = new ArrayList<>();
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row next = iterator.next();
            if (next.getRowNum() <= headerRowIndex) {
                continue;
            }
            Map<String, String> rowMap = new HashMap();
            Iterator<Cell> cellIterator = next.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String headValue = getValue(headRow.getCell(cell.getColumnIndex()));
                String columnValue = getValue(cell);
                if (formatter != null) {
                    rowMap.put(formatter.formatting(headValue), columnValue);
                } else {
                    rowMap.put(headValue, columnValue);
                }
            }
            //避免空行
            Collection collection = rowMap.values();
            collection.removeAll(Collections.singleton(null));
            if (!CollectionUtils.isEmpty(collection)) {
                T convertValue = convertor.convert(rowMap);
                if (convertValue != null) {
                    dataList.add(convertValue);
                }
            }

        }

        System.out.println("读取完毕耗时:" + (System.currentTimeMillis() - _time) / 1000);

        return dataList;
    }

    private static String getValue(Cell cell) {
        try {
            if (null == cell) {
                return null;
            }
            if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            } else if (cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellTypeEnum().equals(CellType.ERROR)) {
                return String.valueOf(cell.getErrorCellValue());
            } else if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
                return cell.getCellFormula();
            } else {
                return trimToNull(cell.getStringCellValue());
            }
        } catch (Exception e) {
            LOGGER.error("row:{} cell:{}", cell.getRowIndex(), cell.getColumnIndex());
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public interface HeaderFormatter {
        String formatting(String key);
    }

    public interface Convertor<T> {
        T convert(final Map<String, String> rowData);
    }

    public interface Filter<T> {
        boolean filter(final T item, final Map<String, String> rowData, final List<T> unmodifiableList);
    }


    private static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    private static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

}
