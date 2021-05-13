package com.example.pinduoduo.selenium.handlers;

import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.selenium.GenericSeleniumHandler;
import com.example.pinduoduo.selenium.HandlerChain;
import com.example.pinduoduo.service.OrderService;
import com.example.pinduoduo.utils.SpringHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 进入已发货页面,点击显示手机号等信息
 */
@Slf4j
public class ShowDeliveredPhoneHandler extends GenericSeleniumHandler {
    @Override
    protected void doHandlerInternal(WebDriver driver, HandlerChain handlerChain) throws Exception {
        OrderService orderService = SpringHolder.getBean(OrderService.class);
        sleepSeconds(1, 3);
        waitUtilGet(driver, "https://mms.pinduoduo.com/print/delivered-order");


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
            WebElement tbody = waitUtilElement(driver, By.cssSelector("tbody[data-testid='beast-core-table-middle-tbody']"));
            retry(5, 5000, () -> {
                List<WebElement> elements = waitUtilElements(tbody, By.tagName("tr"));
                if (elements == null || elements.isEmpty()) {
                    throw new RuntimeException("没有数据");
                }
                // 开始点击显示手机号
                for (int i = 0; i < elements.size(); i++) {
                    WebElement element = elements.get(i);
                    final int rowNum = i + 1;
                    retry(5, 5000, () -> {
                        List<WebElement> tdList = element.findElements(By.tagName("td"));
                        if (tdList.size() > 3) {
                            WebElement td = tdList.get(3);
                            String orderNo = waitUtilElement(tdList.get(1), By.xpath("div/span")).getText();
                            if (orderService.existsByOrderNo(orderNo)) {
                                log.info("序号{} 订单号: {} 已存在", rowNum, orderNo);
                                return;
                            }
                            sleepSeconds(3, 5);
                            String username = waitUtilElement(td, By.xpath("div/div")).getText();
                            if (username.contains("*") && !username.equals("**")) { // 排除 ** 之外的触发点击
                                waitUtil(() -> waitUtilElement(td, By.cssSelector("i[data-testid='beast-core-icon-lock']"))).click();
                                Thread.sleep(2000);
                                username = waitUtilElement(td, By.xpath("div/div")).getText();
                            }
                            log.info("获取到: 第" + rowNum + "行  " + username);

                            // 只有一个*的话,就触发重试点击
                            if (!username.equals("**") && username.contains("*")) {
                                throw new RuntimeException("重试..." + username);
                            }

                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setOrderNo(orderNo);
                            orderInfo.setSendTime(tdList.get(2).getText());
                            orderInfo.setReceiver(waitUtilElement(tdList.get(3), By.xpath("div/div")).getText());
                            orderInfo.setPhone(waitUtilElement(tdList.get(4), By.xpath("div/div")).getText());
                            orderInfo.setCity(tdList.get(5).getText());
                            orderInfo.setAddress(waitUtilElement(tdList.get(6), By.xpath("div/div")).getText());
                            orderInfo.setProductName(tdList.get(7).getText());
                            orderInfo.setProductSpec(tdList.get(8).getText());
                            orderInfo.setSku(tdList.get(9).getText());
                            orderInfo.setNum(Integer.parseInt(tdList.get(10).getText()));
                            orderInfo.setTotalAmount(new BigDecimal(tdList.get(11).getText()));
                            orderInfo.setPayAmount(new BigDecimal(tdList.get(12).getText()));
                            orderInfo.setCourier(tdList.get(13).getText());
                            orderInfo.setCourierNo(StringUtils.replace(waitUtilElement(tdList.get(14), By.xpath("span")).getText(), "物流信息", ""));
                            orderInfo.setRemark(tdList.get(15).getText());
                            orderService.saveOrderInfo(orderInfo);
                        }
                    });
                }
//                // 生成一页保存一个xls
//                MapExporter export = new MapExporter("订单信息", dataList);
//                SXSSFWorkbook workbook = export.writeBook();
//                workbook.write(new FileOutputStream("output/" + DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".xlsx"));
//                workbook.close();
            });

            sleepSeconds(1, 3);

            // 开始翻页
            WebElement pageNext = waitUtilElement(driver, By.cssSelector("li[data-testid='beast-core-pagination-next']"));
            if (pageNext == null || pageNext.getAttribute("class").contains("disabled")) {
                return;
            }
            pageNext.click();
        }


    }
}