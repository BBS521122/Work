package com.work.work.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * MinioProperties 类用于绑定 MinIO 配置属性。
 * 通过 @ConfigurationProperties 注解，将配置文件中的属性映射到该类的字段。
 */
@Component
@ConfigurationProperties(prefix = "minio") // 绑定配置前缀
public class MinioProperties {
    /**
     * MinIO 服务的访问端点。
     */
    private String endpoint;

    /**
     * MinIO 服务的访问密钥。
     */
    private String accessKey;

    /**
     * MinIO 服务的密钥。
     */
    private String secretKey;

    /**
     * MinIO 服务的默认存储桶名称。
     */
    private String bucket;

    private long partSize;
    private Duration urlExpiry = Duration.ofHours(1);

    /**
     * 获取 MinIO 服务的访问端点。
     *
     * @return MinIO 服务的访问端点
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * 设置 MinIO 服务的访问端点。
     *
     * @param endpoint MinIO 服务的访问端点
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 获取 MinIO 服务的访问密钥。
     *
     * @return MinIO 服务的访问密钥
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * 设置 MinIO 服务的访问密钥。
     *
     * @param accessKey MinIO 服务的访问密钥
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * 获取 MinIO 服务的密钥。
     *
     * @return MinIO 服务的密钥
     */
    public String getSecretKey() {
        return secretKey;
    }

    public long getPartSize() {
        return partSize;
    }

    /**
     * 设置 MinIO 服务的密钥。
     *
     * @param secretKey MinIO 服务的密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取 MinIO 服务的默认存储桶名称。
     *
     * @return MinIO 服务的默认存储桶名称
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * 设置 MinIO 服务的默认存储桶名称。
     *
     * @param bucket MinIO 服务的默认存储桶名称
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public Duration getUrlExpiry() {
        return urlExpiry;
    }

    public void setUrlExpiry(Duration urlExpiry) {
        this.urlExpiry = urlExpiry;
    }
}