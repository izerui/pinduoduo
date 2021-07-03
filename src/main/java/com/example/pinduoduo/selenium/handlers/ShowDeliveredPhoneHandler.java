package com.example.pinduoduo.selenium.handlers;

import com.example.pinduoduo.entity.OrderInfo;
import com.example.pinduoduo.selenium.GenericSeleniumHandler;
import com.example.pinduoduo.selenium.HandlerChain;
import com.example.pinduoduo.service.OrderService;
import com.example.pinduoduo.utils.SpringHolder;
import com.example.pinduoduo.utils.StringRandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;

import java.math.BigDecimal;
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
        Thread.sleep(5000);

        int page = 1;
        while (true) {
            log.info("开始爬取第" + page + "页");
            // 查找未显示手机号的按钮
            WebElement tbody = waitUtilElement(driver, By.cssSelector("tbody[data-testid='beast-core-table-middle-tbody']"));
            retry(5, 3500, () -> {
                List<WebElement> elements = waitUtilElements(tbody, By.tagName("tr"));
                if (elements == null || elements.isEmpty()) {
                    throw new RuntimeException("没有数据");
                }
                // 开始点击显示手机号
                for (int i = 0; i < elements.size(); i++) {
                    WebElement element = elements.get(i);
                    final int rowNum = i + 1;
                    retry(10, 3500, () -> {
                        List<WebElement> tdList = element.findElements(By.tagName("td"));
                        if (tdList.size() > 3) {
                            WebElement td = tdList.get(3);
                            WebElement orderNoWebElement = waitUtilElement(tdList.get(1), By.xpath("div/span"));
                            String orderNo = orderNoWebElement.getText();
                            if (orderService.existsByOrderNo(orderNo)) {
//                                String existNotify = String.format("序号%s 订单号: %s 已存在", rowNum, orderNo);
//                                WebElement notifyEl = waitUtilElement(driver, By.cssSelector("div[data-testid='beast-core-noticeBar']")).findElement(By.xpath("div/div/div"));
//                                ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('innerHTML', '" + existNotify + "');", notifyEl);
                                log.info("序号{} 订单号: {} 已存在", rowNum, orderNo);
                                return;
                            }
                            String username = waitUtilElement(td, By.xpath("div/div")).getText();
                            String phoneNo = waitUtilElement(tdList.get(4), By.xpath("div/div")).getText();
                            log.info("---> 正在获取: 第" + rowNum + "行  " + username + " : " + phoneNo);
                            if (phoneNo.contains("*")) { // 如果手机号没显示则触发点击显示手机号
                                Thread.sleep(8000);
                                WebElement webElement = td.findElement(By.cssSelector("i[data-testid='beast-core-icon-lock']"));
//                                WebElement webElement = waitUtil(() -> waitUtilElement(td, By.cssSelector("i[data-testid='beast-core-icon-lock']")));
                                if (webElement != null) {
                                    webElement.click();
                                    Thread.sleep(1000);
                                }

                            }
                            username = waitUtilElement(td, By.xpath("div/div")).getText();
                            phoneNo = waitUtilElement(tdList.get(4), By.xpath("div/div")).getText();

                            if (phoneNo.contains("#")) {
                                try {
                                    WebElement cancelDialog = driver.findElement(By.cssSelector("button[data-testid='beast-core-modal-close-button']"));
                                    if (cancelDialog != null) {
                                        cancelDialog.click();
                                        Thread.sleep(1000);
                                    }
                                } catch (Exception e) {
                                    ;
                                }
                                List<WebElement> divList = ((RemoteWebElement) tdList.get(4)).findElementsByXPath("div");
                                Actions actionProvider = new Actions(driver);
                                // Performs mouse move action onto the element
                                actionProvider.moveToElement(divList.get(1)).build().perform();
                                // 点击号码保护 弹出报备弹框
                                WebElement formElement = waitUtilElement(driver, By.cssSelector("div[data-testid='beast-core-portal-main']"));
                                formElement.click();
                                WebElement haomabaohuButton = waitUtilElements(formElement, By.xpath("div/div/div/div/div/div")).get(1);
                                WebElement span = waitUtilElement(haomabaohuButton, By.tagName("span"));
                                Thread.sleep(1000);
                                span.click();

                                // 消除号码保护提示框
                                waitUtilElement(driver, By.cssSelector("div[data-testid='beast-core-modal-inner']")).click();

                                // 开始报备
                                Thread.sleep(1000);
                                List<WebElement> baobeiyuanyin = waitUtilElements(driver, By.cssSelector("label[data-testid='beast-core-radio']"));
                                // 选择线下手动发货
                                WebElement xianxiafahuoRadio = baobeiyuanyin.get(0);
                                Thread.sleep(1000);
                                xianxiafahuoRadio.click();
                                // 填入随机字符
                                Thread.sleep(1000);
                                WebElement textInput = waitUtilElement(driver, By.cssSelector("textarea[data-testid='beast-core-textArea-htmlInput']"));
                                textInput.sendKeys(StringRandomUtils.getRandomJianHan());
                                // 提交报备
                                Thread.sleep(10000);
                                waitUtilElement(driver, By.cssSelector("button[data-testid='beast-core-modal-ok-button']")).click();
                                Thread.sleep(1000);
                                phoneNo = waitUtilElement(tdList.get(4), By.xpath("div/div")).getText();
                            }

                            OrderInfo orderInfo = new OrderInfo();
                            orderInfo.setOrderNo(orderNo);
                            DateTime sendDateTime = DateTime.parse(tdList.get(2).getText(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                            orderInfo.setSendDate(sendDateTime.toString("yyyy-MM-dd"));
                            orderInfo.setSendTime(sendDateTime.toDate());
                            orderInfo.setReceiver(waitUtilElement(tdList.get(3), By.xpath("div/div")).getText());
                            orderInfo.setPhone(phoneNo);
                            if (orderInfo.getPhone().contains("#")) {
                                throw new RuntimeException("未获取到真实手机号,重新获取...");
                            }
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
                            log.info("<--- 获取成功: 第" + rowNum + "行  " + username + " : " + phoneNo);
                        }
                    });
                }
//                // 生成一页保存一个xls
//                MapExporter export = new MapExporter("订单信息", dataList);
//                SXSSFWorkbook workbook = export.writeBook();
//                workbook.write(new FileOutputStream("output/" + DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".xlsx"));
//                workbook.close();
            });

            // 开始翻页
            WebElement pageNext = waitUtilElement(driver, By.cssSelector("li[data-testid='beast-core-pagination-next']"));
            if (pageNext == null || pageNext.getAttribute("class").contains("disabled")) {
                return;
            }
            pageNext.click();
            Thread.sleep(2000);
            page++;
        }


    }


    private void retry______(WebDriver driver, Runnable... runnables) {
        WebElement element = null;
        try {
            Thread.sleep(1000);
            element = driver.findElement(By.cssSelector("div[data-testid='beast-core-icon-warning-circle_filled']"));
            if (element != null) {
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            if (element != null) {
                log.error(element.getText());
            }
        } finally {
            if (runnables != null) {
                for (Runnable runnable : runnables) {
                    runnable.run();
                }
            }
        }

    }
}
