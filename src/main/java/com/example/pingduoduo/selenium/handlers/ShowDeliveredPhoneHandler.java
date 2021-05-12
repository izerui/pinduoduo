package com.example.pingduoduo.selenium.handlers;

import com.example.pingduoduo.selenium.GenericSeleniumHandler;
import com.example.pingduoduo.selenium.HandlerChain;
import com.example.pingduoduo.utils.MapExporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 进入已发货页面,点击显示手机号等信息
 */
@Slf4j
public class ShowDeliveredPhoneHandler extends GenericSeleniumHandler {
    @Override
    protected void doHandlerInternal(WebDriver webDriver, HandlerChain handlerChain) throws Exception {
        sleepSeconds(1, 3);
        webDriver.get("https://mms.pinduoduo.com/print/delivered-order");


//        // 选择时间范围
//        WebElement timeBetween = new WebDriverWait(driver, 10l)
//                .until(webDriver -> webDriver.findElement(By.cssSelector("input[data-testid='beast-core-rangePicker-htmlInput']")));
//        timeBetween.click();
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='2021-05-07 00:00:00 ~ 2021-05-07 10:00:00';", timeBetween);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', '2021-05-07 00:00:00 ~ 2021-05-07 9:00:00');", timeBetween);


        // 人工介入选择时间
        Thread.sleep(20000);


        while (true) {

            // 查找未显示手机号的按钮
            WebElement tbody = waitUtil(() -> webDriver.findElement(By.cssSelector("tbody[data-testid='beast-core-table-middle-tbody']")));
            retry(5, 5000, () -> {
                List<WebElement> elements = waitUtil(() -> tbody.findElements(By.tagName("tr")));
                if (elements == null || elements.isEmpty()) {
                    throw new RuntimeException("没有数据");
                }

                List<LinkedHashMap<String, String>> dataList = new ArrayList<>();

                // 开始点击显示手机号
                for (int i = 0; i < elements.size(); i++) {
                    WebElement element = elements.get(i);
                    sleepSeconds(3, 5);
                    final int rowNum = i + 1;
                    retry(5, 5000, () -> {
                        List<WebElement> tdList = element.findElements(By.tagName("td"));
                        if (tdList.size() > 3) {
                            WebElement td = tdList.get(3);
                            String username = td.findElement(By.xpath("div/div")).getText();
                            if (!username.equals("**")) { // ** 不触发点击
                                waitUtil(() -> td.findElement(By.cssSelector("i[data-testid='beast-core-icon-lock']"))).click();
                                Thread.sleep(3000);
                                username = td.findElement(By.xpath("div/div")).getText();
                            }
                            log.info("获取到: 第" + rowNum + "行  " + username);
                            // 只有一个*的话,就触发点击
                            if (!username.equals("**") && username.contains("*")) {
                                throw new RuntimeException("重试..." + username);
                            }

                            LinkedHashMap<String, String> map = new LinkedHashMap<>();
                            map.put("订单号", tdList.get(1).findElement(By.xpath("div/span")).getText());
                            map.put("发货时间", tdList.get(2).getText());
                            map.put("收件人", tdList.get(3).findElement(By.xpath("div/div")).getText());
                            map.put("手机号", tdList.get(4).findElement(By.xpath("div/div")).getText());
                            map.put("省市区", tdList.get(5).getText());
                            map.put("详细地址", tdList.get(6).findElement(By.xpath("div/div")).getText());
                            map.put("商品名称", tdList.get(7).getText());
                            map.put("规格", tdList.get(8).getText());
                            map.put("sku编码", tdList.get(9).getText());
                            map.put("商品数量", tdList.get(10).getText());
                            map.put("总价", tdList.get(11).getText());
                            map.put("支付金额", tdList.get(12).getText());
                            map.put("快递", tdList.get(13).getText());
                            map.put("快递单号", StringUtils.replace(tdList.get(14).findElement(By.xpath("span")).getText(), "物流信息", ""));
                            map.put("备注", tdList.get(15).getText());
                            dataList.add(map);
                        }
                    });

                }

                // 生成一页保存一个xls
                MapExporter export = new MapExporter("订单信息", dataList);
                SXSSFWorkbook workbook = export.writeBook();
                workbook.write(new FileOutputStream("output/" + DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".xlsx"));
                workbook.close();
            });

            sleepSeconds(1, 3);

            // 开始翻页
            WebElement pageNext = webDriver.findElement(By.cssSelector("li[data-testid='beast-core-pagination-next']"));
            if (pageNext == null || pageNext.getAttribute("class").contains("disabled")) {
                break;
            }
            pageNext.click();
        }


    }
}
