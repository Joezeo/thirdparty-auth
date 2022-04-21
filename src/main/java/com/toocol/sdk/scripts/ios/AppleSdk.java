package com.toocol.sdk.scripts.ios;

import com.alibaba.fastjson.JSONObject;
import com.toocol.sdk.constants.SdkConstants;
import com.toocol.sdk.core.AbstractSdkScript;
import com.toocol.sdk.core.HttpClient;
import com.toocol.sdk.core.Sdk;
import com.toocol.sdk.core.StrUtil;

/**
 * <a href="https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple">
 *     apple_rest_api/authenticating_users_with_sign_in_with_apple</a><br>
 * <a href="https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens">sign_in_with_apple/generate_and_validate_tokens</a><br>
 * <a href="https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user">apple_rest_api/verifying_a_user</a><br>
 *
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/13 16:31
 */
@Sdk(name = SdkConstants.APPLE)
public class AppleSdk extends AbstractSdkScript {

    private static final String CLIENT_ID = "client id";
    private static final String CLIENT_SECRET = "client secret";

    private static final String AUTH_URL = "https://appleid.apple.com/auth/token?client_id={}&client_secret={}&code={}&grant_type=authorization_code&redirect_uri=REDIRECT_URI";

    protected AppleSdk(HttpClient httpClient) {
        super(httpClient);
    }

    /**
     * 客户端拉取苹果授权页面, 用户授权登录后, 可以取到玩家的信息
     * "After successfully authenticating the user, the server returns an identity token, authorization code, and user identifier to your app."<br><br>
     * 此时获取到的identity token(属于JWT数据格式需要自行解析)，已经可以根据 Apple 公钥进行验证了，但是需要客户端传送至服务器端, 存在被篡改的风险，故选择客户端将authorization code传送到服务器，
     * 由服务器二次获取identity_token。<br>
     * <a href="https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens">sign_in_with_apple/generate_and_validate_tokens</a><br>
     * 请求示例:<br>
     * curl -v POST "https://appleid.apple.com/auth/token" \ <br>
     * -H 'content-type: application/x-www-form-urlencoded' \ <br>
     * -d 'client_id=CLIENT_ID' \ <br>
     * -d 'client_secret=CLIENT_SECRET' \ <br>
     * -d 'code=CODE' \ <br>
     * -d 'grant_type=authorization_code' \ <br>
     * -d 'redirect_uri=REDIRECT_URI' <br>
     * 其返回值中的id_token即为identity token<br>
     */
    @Override
    public JSONObject verifyAccount(JSONObject param) {
        String authorizationCode = param.getString("authorization_code");
        // 即用户id
        String identifier = param.getString("identifier");

        String url = StrUtil.fulfill(AUTH_URL, CLIENT_ID, CLIENT_SECRET, authorizationCode);
        JSONObject httpResult = httpClient.doGet(url);
        if (httpResult == null) {
            System.out.println(StrUtil.fulfill("<apple> verify account失败, http返回值为null, identifier = {}, authorizationCode = {}", identifier, authorizationCode));
            return null;
        }
        // TODO: 获取并且接卸JWT数据: identity token
        return null;
    }

    @Override
    public JSONObject verifyOrder(JSONObject jsonObject) {
        return null;
    }

}
