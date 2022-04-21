package com.toocol.sdk.scripts.ios;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.toocol.sdk.constants.SdkConstants;
import com.toocol.sdk.core.AbstractSdkScript;
import com.toocol.sdk.core.HttpClient;
import com.toocol.sdk.core.Sdk;
import com.toocol.sdk.core.StrUtil;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

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

    /**
     * 签发人
     */
    private static final String ISSUER = "https://appleid.apple.com";

    private static final String CLIENT_ID = "client id";
    private static final String CLIENT_SECRET = "{\n" +
            "    \"alg\": \"ES256\",\n" +
            "    \"kid\": \"ABC123DEFG\"\n" +
            "}\n" +
            "{\n" +
            "    \"iss\": \"DEF123GHIJ\",\n" +
            "    \"iat\": 1437179036,\n" +
            "    \"exp\": 1493298100,\n" +
            "    \"aud\": \"https://appleid.apple.com\",\n" +
            "    \"sub\": \"com.mytest.app\"\n" +
            "}";

    private static final String JWK_PUBLIC_KEYS = "{\n" +
            "  \"keys\": [\n" +
            "    {\n" +
            "      \"kty\": \"RSA\",\n" +
            "      \"kid\": \"86D88Kf\",\n" +
            "      \"use\": \"sig\",\n" +
            "      \"alg\": \"RS256\",\n" +
            "      \"n\": \"iGaLqP6y-SJCCBq5Hv6pGDbG_SQ11MNjH7rWHcCFYz4hGwHC4lcSurTlV8u3avoVNM8jXevG1Iu1SY11qInqUvjJur--hghr1b56OPJu6H1iKulSxGjEIyDP6c5BdE1uwprYyr4IO9th8fOwCPygjLFrh44XEGbDIFeImwvBAGOhmMB2AD1n1KviyNsH0bEB7phQtiLk-ILjv1bORSRl8AK677-1T8isGfHKXGZ_ZGtStDe7Lu0Ihp8zoUt59kx2o9uWpROkzF56ypresiIl4WprClRCjz8x6cPZXU2qNWhu71TQvUFwvIvbkE1oYaJMb0jcOTmBRZA2QuYw-zHLwQ\",\n" +
            "      \"e\": \"AQAB\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"kty\": \"RSA\",\n" +
            "      \"kid\": \"eXaunmL\",\n" +
            "      \"use\": \"sig\",\n" +
            "      \"alg\": \"RS256\",\n" +
            "      \"n\": \"4dGQ7bQK8LgILOdLsYzfZjkEAoQeVC_aqyc8GC6RX7dq_KvRAQAWPvkam8VQv4GK5T4ogklEKEvj5ISBamdDNq1n52TpxQwI2EqxSk7I9fKPKhRt4F8-2yETlYvye-2s6NeWJim0KBtOVrk0gWvEDgd6WOqJl_yt5WBISvILNyVg1qAAM8JeX6dRPosahRVDjA52G2X-Tip84wqwyRpUlq2ybzcLh3zyhCitBOebiRWDQfG26EH9lTlJhll-p_Dg8vAXxJLIJ4SNLcqgFeZe4OfHLgdzMvxXZJnPp_VgmkcpUdRotazKZumj6dBPcXI_XID4Z4Z3OM1KrZPJNdUhxw\",\n" +
            "      \"e\": \"AQAB\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

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
        return null;
    }

    public static void main(String[] args) throws Exception {
        JSONArray keys = JSON.parseObject(JWK_PUBLIC_KEYS).getJSONArray("keys");
        JSONObject jwk = null;
        for (Object key : keys) {
            JSONObject jo = (JSONObject) key;
            // 生产环境中kid从client secret中获取
            if (jo.getString("kid").equals("86D88Kf")) {
                jwk = jo;
            }
        }
        if (jwk == null) {
            return;
        }

        BigInteger modulus = new BigInteger(1, Base64.decodeBase64(jwk.getString("n")));
        BigInteger exponent = new BigInteger(1, Base64.decodeBase64(jwk.getString("e")));
        RSAPublicKey pub = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));

        // 一个网上的示例token, 已经过期
        String testToken = "eyJraWQiOiI4NkQ4OEtmIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLmNoYW5nZGFvLnR0c2Nob29sIiwiZXhwIjoxNTg5MTg1Mjg1LCJpYXQiOjE1ODkxODQ2ODUsInN1YiI6IjAwMTk0MC43YTExNDFhYTAwMWM0NjllYTE1NjNjNmJhZTk5YzM3ZC4wMzA3IiwiY19oYXNoIjoiN1gzc2x2dHVBU0kwYmFSbU0wVGFrQSIsImVtYWlsIjoiYXEzMmsydnpjd0Bwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6InRydWUiLCJpc19wcml2YXRlX2VtYWlsIjoidHJ1ZSIsImF1dGhfdGltZSI6MTU4OTE4NDY4NSwibm9uY2Vfc3VwcG9ydGVkIjp0cnVlfQ.S9wCOt6EeOoRrSMq4kUkPgJPyP1ruMXEcEZeeQEd1CDpcyVWLI8nTOqrl-l0sWYR-5nl2-1iJyiu77fRv8T7dBoV0EHT7GgM1l7qhnWsI9I8V-56rA9ArdJrLIBJbxu7j-xzQhZb6PZ5MSxPZ6WqZay0RpP9JiQ23ybssWQsMnqzvVZkye0iNtBGT1LnfT80XNxmj8L2uJZY08mXjjWWsYY_h0_IRvqOLyaW99w-F8T9KuDkWz2Z-DJX_tiKC0DOT03ypBv82H0v_v-8lFlp4rNRSB82CdgfYwEWElU7zKZfaHJOxT3wOvRXNpbj6_hENPdbtG2ozgdg2oVEiamz0g";
        Algorithm algorithm = Algorithm.RSA256(pub, null);

        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        DecodedJWT jwt = jwtVerifier.verify(testToken);
        System.out.println("header: " + jwt.getHeader());
        System.out.println("payload: " + jwt.getPayload());
        System.out.println("token: " + jwt.getToken());
    }

    @Override
    public JSONObject verifyOrder(JSONObject jsonObject) {
        return null;
    }

}
