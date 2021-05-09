package com.example.pingduoduo.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NutchUtils {

    private final static Logger log = LoggerFactory.getLogger(NutchUtils.class);

    private final static OkHttpClient okHttpClient = new OkHttpClient();

    private static Request.Builder createRequest(String anti, String cookie) {
        Request.Builder builder = new Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .header("anti-content", anti)
                .header("origin", "https://mms.pinduoduo.com")
                .header("referer", "https://mms.pinduoduo.com/orders/list")
                .header("Cookie", cookie);
        return builder;
    }

    public static String postJson(String url, String body, String anti, String cookie) throws IOException {
        Request request = createRequest(anti,cookie)
                .url(url)
                .post(RequestBody.create(MediaType.get(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE), body))
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

//    public static void main(String[] args) {
//        System.out.println(System.currentTimeMillis());
//        System.out.println("1620192070");
//        DateTime dateTime = new DateTime(new Date(1620192070000L));
//        System.out.println(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
//    }

}
