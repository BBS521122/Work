package com.work.work.config;

import com.work.work.properties.MinioProperties;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class MinioConfigTest {

    private MinioConfig minioConfig;

    @Mock
    private MinioProperties minioProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(minioProperties.getEndpoint()).thenReturn("http://localhost:9000");
        when(minioProperties.getAccessKey()).thenReturn("minioadmin");
        when(minioProperties.getSecretKey()).thenReturn("minioadmin");
        minioConfig = new MinioConfig(minioProperties);
    }

    @Test
    void minioClientBeanCreation() {
        MinioClient minioClient = minioConfig.minioClient();
        assertNotNull(minioClient);
    }
}

