package com.toocol.sdk.core;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/3/14 16:25
 */
public abstract class AbstractSdkScript {

    protected final HttpClient httpClient;

    protected AbstractSdkScript(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 向第三方请求验证账号
     *
     * @param jsonObject 请求参数
     * @return 验证成功返回JSONObject, 失败返回null
     */
    public abstract JSONObject verifyAccount(JSONObject jsonObject);

    /**
     * 向第三方请求验证账号
     *
     * @param jsonObject 请求参数
     * @return 验证成功返回JSONObject, 失败返回null
     */
    public abstract JSONObject verifyOrder(JSONObject jsonObject);

}
