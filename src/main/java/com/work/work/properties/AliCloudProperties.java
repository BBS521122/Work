package com.work.work.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ali-cloud")
public class AliCloudProperties {
    private String ak;
    private String sk;
    private String endpoint;
    private String bucketName;
    private String appkey;

    public AliCloudProperties() {
    }

    public AliCloudProperties(String ak, String sk, String endpoint, String bucketName, String appkey) {
        this.ak = ak;
        this.sk = sk;
        this.endpoint = endpoint;
        this.bucketName = bucketName;
        this.appkey = appkey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
