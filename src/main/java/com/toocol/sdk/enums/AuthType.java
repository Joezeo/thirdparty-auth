package com.toocol.sdk.enums;

import com.toocol.sdk.constants.SdkConstants;

/**
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/15 11:06
 */
public enum AuthType {
    /**
     * facebook
     */
    FACEBOOK(SdkConstants.FACEBOOK, 1),
    /**
     * google
     */
    GOOGLE(SdkConstants.GOOGLE, 2),
    /**
     * ios
     */
    IOS(SdkConstants.APPLE, 3)
    ;

    public final String name;

    public final int id;

    AuthType(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
