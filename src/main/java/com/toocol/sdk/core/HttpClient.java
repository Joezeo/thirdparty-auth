package com.toocol.sdk.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/14 19:19
 */
public class HttpClient {

    private OkHttpClient okHttpClient;

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }

    public JSONObject doGet(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if(response.code() != 200) {
                return null;
            }
            return JSON.parseObject(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Get请求返回结果为null, url = " + url);
        }

        return null;
    }
}
