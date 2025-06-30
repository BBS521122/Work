package com.work.work.config;

import com.work.work.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Autowired
    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://" + getLocalIp() + ":9000")
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
    private String getLocalIp() {
        try {
            java.net.InetAddress inetAddress = java.net.InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("无法获取本地IP", e);
        }
    }


}
