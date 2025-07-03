package com.work.work.config;

import com.work.work.properties.MinioProperties;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Autowired
    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    @Bean
    public MinioClient minioClient() throws Exception {
        // 创建自定义 TrustManager
        X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        };

        // 创建 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

        // 配置 OkHttpClient
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager) // 传入非 null 的 trustManager
                .hostnameVerifier((hostname, session) -> true)
                .build();

        return MinioClient.builder()
                .endpoint("https://192.168.139.1", 9000, true)
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .httpClient(httpClient)
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
