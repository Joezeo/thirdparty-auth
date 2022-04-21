package com.toocol.sdk.scripts.facebook;

import com.alibaba.fastjson.JSONObject;
import com.toocol.sdk.constants.SdkConstants;
import com.toocol.sdk.core.AbstractSdkScript;
import com.toocol.sdk.core.HttpClient;
import com.toocol.sdk.core.Sdk;
import com.toocol.sdk.core.StrUtil;

/**
 * <a href="https://developers.facebook.com/docs/facebook-login/guides/access-tokens">access-tokens</a><br>
 * <a href="https://developers.facebook.com/docs/facebook-login/security">security</a><br>
 * <a href="https://developers.facebook.com/docs/facebook-login/security#proof">security#proof</a><br>
 * <a href="https://developers.facebook.com/docs/facebook-login/auth-vs-data">auth-vs-data</a><br>
 * <a href="https://developers.facebook.com/docs/facebook-login/guides/%20access-tokens/debugging">access-tokens/debugging</a>
 *
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/13 13:51
 */
@Sdk(name = SdkConstants.FACEBOOK)
public class FacebookSdk extends AbstractSdkScript {

    /**
     * input_token为用户登录后获取的access_token(用户访问口令); access_token为app的应用访问口令
     * <br>
     * <br>
     * 应用访问口令access_token两种获取方式：
     * <ul>
     * <li>Get请求获取: https://graph.facebook.com/oauth/access_token?client_id={your-app_id}&client_secret={your-app_secret}&grant_type=client_credentials</li>
     * <li> 在发出调用时以 access_token 参数的形式发送应用编号和应用密钥: https://graph.facebook.com/{api-endpoint}&access_token={your-app_id}|{your-app_secret}</li>
     * </ul>
     */
    private static final String DEBUG_BASE_VERIFY_ACCOUNT_URL = "https://graph.facebook.com/debug_token?access_token={}&input_token={}";

    private static final String APP_ID = "app id";
    private static final String APP_SECRET = "app secret";

    public FacebookSdk(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * 客户端先拉取facebook授权页面，等用户授权登录后，取到玩家的facebook id 和 access_token(用户访问口令)，然后将这个id和access_token传给服务端
     *
     * @param jsonObject 请求参数
     * @return JSONObject
     */
    @Override
    public JSONObject verifyAccount(JSONObject jsonObject) {
        if (!jsonObject.containsKey("channelId")) {
            System.out.println("channelId不存在");
            return null;
        }
        if (!jsonObject.containsKey("facebookId")) {
            System.out.println("facebookId不存在");
            return null;
        }
        if (!jsonObject.containsKey("token")) {
            System.out.println("token不存在");
            return null;
        }

        String facebookId = jsonObject.getString("facebookId");
        String token = jsonObject.getString("token");

        String url = StrUtil.fulfill(DEBUG_BASE_VERIFY_ACCOUNT_URL, APP_ID + "|" + APP_SECRET, token);
        JSONObject httpResult = httpClient.doGet(url);
        if (httpResult == null) {
            System.out.println(StrUtil.fulfill("<facebook> verify account失败, 返回值为null, facebookId = {}, token = {}", facebookId, token));
            return null;
        }
        JSONObject data = httpResult.getJSONObject("data");
        if (data == null) {
            System.out.println(StrUtil.fulfill("<facebook> verify account失败, data为null, facebookId = {}, token = {}", facebookId, token));
            return null;
        }
        boolean isValid = data.getBooleanValue("is_valid");
        if (!isValid) {
            System.out.println(StrUtil.fulfill("<facebook> verify account失败, is_valid=false, facebookId = {}, token = {}", facebookId, token));
            return null;
        }
        String userId = data.getString("user_id");
        if (!facebookId.equals(userId)) {
            System.out.println(StrUtil.fulfill("<facebook> verify account失败, 用户id不匹配, facebookId = {}, token = {}, verifiedId = {}", facebookId, token, userId));
            return null;
        }

        JSONObject verifyResult = new JSONObject();
        verifyResult.put("identifier", userId);
        return verifyResult;
    }

    @Override
    public JSONObject verifyOrder(JSONObject jsonObject) {
        return null;
    }

}
