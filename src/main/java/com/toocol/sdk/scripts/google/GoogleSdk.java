package com.toocol.sdk.scripts.google;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.toocol.sdk.constants.SdkConstants;
import com.toocol.sdk.core.AbstractSdkScript;
import com.toocol.sdk.core.HttpClient;
import com.toocol.sdk.core.Sdk;

import java.util.Collections;

/**
 *
 * 两种接入方案:<br>
 * 1. <a href="https://developers.google.com/identity/one-tap/android/idtoken-auth">android/idtoken-auth</a><br>
 * 2. <a href="https://developers.google.com/identity/protocols/oauth2/openid-connect">oauth2/openid-connect</a><br>
 * <br>
 * 方案2需要在谷歌开发者账号配置后台回调地址，比较麻烦，综合考虑采取方案1
 *
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/13 16:30
 */
@Sdk(name = SdkConstants.GOOGLE)
public class GoogleSdk extends AbstractSdkScript {

    private static final String CLIENT_ID = "";

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(CLIENT_ID))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

    protected GoogleSdk(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * 客户端拉取授权登录页面, 登陆成功后将获取的token传到服务器, 服务器调用GoogleSDK进行验证
     */
    @Override
    public JSONObject verifyAccount(JSONObject jsonObject) {
        if (!jsonObject.containsKey("channelId")) {
            System.out.println("channelId不存在");
            return null;
        }
        if (!jsonObject.containsKey("token")) {
            System.out.println("token不存在");
            return null;
        }
        String token = jsonObject.getString("token");

        JSONObject verifyResult = new JSONObject();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            verifyResult.put("identifier", userId);

            // Get profile information from payload
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
        } else {
            System.out.println("<google> Invalid ID token.");
            return null;
        }

        return verifyResult;
    }

    @Override
    public JSONObject verifyOrder(JSONObject jsonObject) {
        return null;
    }

}
