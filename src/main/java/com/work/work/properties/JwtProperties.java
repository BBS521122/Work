package com.work.work.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * JwtProperties 类用于加载和存储与 JWT相关的配置属性。
 * 该类通过 @ConfigurationProperties 注解从配置文件中读取以 "jwt" 为前缀的属性。
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * 用于签名和验证 JWT 的密钥。
     */
    private String secretKey;

    /**
     * JWT 的有效时间（以毫秒为单位）。
     */
    private long ttl;

    /**
     * JWT 的名称，用于标识令牌。
     */
    private String tokenName;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
