package com.example.pingduoduo.selenium;

import com.example.pingduoduo.utils.MapExporter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.support.RetryTemplate;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.example.pingduoduo.selenium.RandomTimers.execute;

public class PinduoduoMms {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "driver/mac/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-blink-features=AutomationControlled");
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy("127.0.0.1:24000");
//        options.setProxy(proxy);
        WebDriver driver = new ChromeDriver(options);
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            driver.quit();
//        }));

        // 打开登录页面
        execute(0, 0, () -> {
            driver.get("https://mms.pinduoduo.com/login");
        });

        // 切换到账户登录
        execute(0, 3, () -> {
            new WebDriverWait(driver, 10L)
                    .until(webDriver -> webDriver.findElement(By.cssSelector("div[class='tab-item last-item']")))
                    .click();
        });

        List<String> userPass = Files.readAllLines(Paths.get("passwd"));

        // 输入账号密码
        execute(1, 3, () -> {
            driver.findElement(By.id("usernameId")).sendKeys(userPass.get(0));
            driver.findElement(By.id("passwordId")).sendKeys(userPass.get(1));
        });


        // 开始提交登录
        execute(0, 3, () -> {
            new WebDriverWait(driver, 10L)
                    .until(webDriver -> webDriver.findElement(By.tagName("button")))
                    .click();
        });


        // 进入打单工具页面
        execute(0, 3, () -> {
            driver.get("https://mms.pinduoduo.com/print/delivered-order");
        });


        // 打单页面 待发货 关闭广告
//        execute(1, 3, () -> {
//            new WebDriverWait(driver, 10L)
//                    .until(webDriver -> webDriver.findElement(By.cssSelector("i[data-testid='beast-core-icon-close']")))
//                    .click();
//        });

        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(5)
                .fixedBackoff(5000)
                .retryOn(RuntimeException.class)
                .build();

        // https://www.w3schools.com/cssref/css_selectors.asp
//        Thread.sleep(3000);

//        // 选择时间范围
//        WebElement timeBetween = new WebDriverWait(driver, 10l)
//                .until(webDriver -> webDriver.findElement(By.cssSelector("input[data-testid='beast-core-rangePicker-htmlInput']")));
//        timeBetween.click();

//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='2021-05-07 00:00:00 ~ 2021-05-07 10:00:00';", timeBetween);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('value', '2021-05-07 00:00:00 ~ 2021-05-07 9:00:00');", timeBetween);


//        Thread.sleep(2000);
//        // 查询
//        new WebDriverWait(driver, 10l)
//                .until(webDriver -> webDriver.findElement(By.cssSelector("div[class='opt-block']")))
//                .findElements(By.tagName("button")).get(0).click();

        // 人工介入选择时间
        Thread.sleep(20000);
        while (true) {

            // 查找未显示手机号的按钮
            WebElement tbody = new WebDriverWait(driver, 10l)
                    .until(webDriver -> webDriver.findElement(By.cssSelector("tbody[data-testid='beast-core-table-middle-tbody']")));
            retryTemplate.execute(retryContext -> {
                List<WebElement> elements = tbody.findElements(By.tagName("tr"));
                if (elements == null || elements.isEmpty()) {
                    throw new RuntimeException("没有数据");
                }

                List<LinkedHashMap<String, String>> dataList = new ArrayList<>();

                // 开始点击显示手机号
                for (int i = 0; i < elements.size(); i++) {
                    System.out.print("第" + i + "行   ");
                    WebElement element = elements.get(i);
                    execute(3, 5, () -> {
                        retryTemplate.execute(context -> {
                            List<WebElement> tdList = element.findElements(By.tagName("td"));
                            if (tdList.size() > 3) {
                                WebElement td = tdList.get(3);
                                String username = td.findElement(By.xpath("div/div")).getText();
                                if (!username.equals("**")) { // ** 不触发点击
                                    td.findElement(By.cssSelector("i[data-testid='beast-core-icon-lock']")).click();
                                    try {
                                        Thread.sleep(3000);
                                    } catch (Exception e) {
                                        ;
                                    }
                                    username = td.findElement(By.xpath("div/div")).getText();
                                }
                                System.out.println("获取到: " + username);
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
//                                System.out.println("");

                            }
                            return null;
                        });

                    });
                }

                // 生成一页保存一个xls
                MapExporter export = new MapExporter("订单信息", dataList);
                SXSSFWorkbook workbook = export.writeBook();
                workbook.write(new FileOutputStream("output/" + DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".xlsx"));
                workbook.close();

                return null;
            });

//
//            // 获取table
//            WebElement table = new WebDriverWait(driver, 10L)
//                    .until(webDriver -> webDriver.findElement(By.tagName("table")));
//
//            // 写入html
//            String folder = "output/html/" + DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "/";
//            FileUtils.writeStringToFile(new File(folder + page + ".html"), table.getAttribute("outerHTML"), "UTF-8");

//            // 下载信息
//            WebElement downloadDiv = new WebDriverWait(driver, 10l)
//                    .until(webDriver -> webDriver.findElement(By.cssSelector("div[class='opt-row']")));
//            List<WebElement> downloadBtns = downloadDiv.findElements(By.tagName("button"));
//            downloadBtns.get(2).click();


            Thread.sleep(RandomUtils.nextInt(1000, 3000));

            // 开始翻页
            WebElement pageNext = new WebDriverWait(driver, 10l)
                    .until(webDriver -> webDriver.findElement(By.cssSelector("li[data-testid='beast-core-pagination-next']")));
            if (pageNext != null && pageNext.getAttribute("class").contains("disabled")) {
                break;
            }
            pageNext.click();
        }


        System.out.println("完成");

//        // find the search button on the page
//        WebElement searchButton = driver.findElement(By
//                .className("search-submit"));
//        // create an action handler
//        Actions actions = new Actions(driver);
//        // use the action handler to move the cursor to given element
//        actions.moveToElement(searchButton).perform();
//
//        // wait until the search field is presented on the webpage and create an
//        // element
//        WebElement searchField = (new WebDriverWait(driver, 10))
//                .until(ExpectedConditions.presenceOfElementLocated(By.name("s")));
//
//        // puts the text "java" into the search field
//        searchField.sendKeys("java");
//        // submit the search (submit the form)
//        searchField.submit();

        // wait 5 seconds and close the browser
        driver.quit();
    }
}
