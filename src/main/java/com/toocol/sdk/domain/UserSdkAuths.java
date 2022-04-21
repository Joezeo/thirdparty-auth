package com.toocol.sdk.domain;

import java.util.Date;

/**
 * @author ZhaoZhe (joezane.cn@gmail.com)
 * @date 2022/4/15 11:02
 */
public class UserSdkAuths {
    /**
     * 用户本地userId
     */
    private Long id;
    /**
     * 所属渠道id
     */
    private Integer channelId;
    /**
     * 三方sdk验证类型 {@link com.toocol.sdk.enums.AuthType}
     */
    private Integer authType;
    /**
     * 三方sdk唯一标识, 即三方sdk的userId
     */
    private String identifier;
    /**
     * 经由三方sdk验证通过的token
     */
    private String token;
    /**
     *  创建时间
     */
    private Date createTime;

    public UserSdkAuths(Long id, Integer authType, String identifier) {
        this.id = id;
        this.authType = authType;
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static UserSdkAuthsBuilder builder() {
        return new UserSdkAuthsBuilder();
    }

    public static final class UserSdkAuthsBuilder {
        private Long id;
        private Integer channelId;
        private Integer authType;
        private String identifier;
        private String token;
        private Date createTime;

        private UserSdkAuthsBuilder() {
        }

        public static UserSdkAuthsBuilder anUserSdkAuths() {
            return new UserSdkAuthsBuilder();
        }

        public UserSdkAuthsBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSdkAuthsBuilder channelId(Integer channelId) {
            this.channelId = channelId;
            return this;
        }

        public UserSdkAuthsBuilder authType(Integer authType) {
            this.authType = authType;
            return this;
        }

        public UserSdkAuthsBuilder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public UserSdkAuthsBuilder token(String token) {
            this.token = token;
            return this;
        }

        public UserSdkAuthsBuilder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public UserSdkAuths build() {
            UserSdkAuths userSdkAuths = new UserSdkAuths(id, authType, identifier);
            userSdkAuths.setChannelId(channelId);
            userSdkAuths.setToken(token);
            userSdkAuths.setCreateTime(createTime);
            return userSdkAuths;
        }
    }
}
