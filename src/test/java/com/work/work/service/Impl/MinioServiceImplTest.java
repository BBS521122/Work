package com.work.work.service.Impl;

import com.work.work.properties.MinioProperties;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @InjectMocks
    private MinioServiceImpl minioService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioService, "minioClient", minioClient);
        ReflectionTestUtils.setField(minioService, "minioProperties", minioProperties);
    }

    @Test
    void uploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioProperties.getPartSize()).thenReturn(5 * 1024 * 1024L); // 5MiB
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(mock(ObjectWriteResponse.class));
        String result = minioService.uploadFile(file);
        assertNotNull(result);
        assertTrue(result.endsWith(".txt"));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_Exception() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioProperties.getPartSize()).thenReturn(5 * 1024 * 1024L); // 5MiB
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("fail"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> minioService.uploadFile(file));
        assertTrue(ex.getMessage().contains("文件上传失败"));
    }

    @Test
    void getSignedUrl_Success() throws Exception {
        String objectName = "test.txt";
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioProperties.getUrlExpiry()).thenReturn(Duration.ofSeconds(60));
        MinioServiceImpl spyService = Mockito.spy(minioService);
        doReturn(true).when(spyService).objectExists(objectName);
        String url = "http://minio/test.txt";
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn(url);
        String result = spyService.getSignedUrl(objectName);
        assertEquals(url, result);
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void getSignedUrl_ObjectNotExist() {
        String objectName = "notfound.txt";
        MinioServiceImpl spyService = Mockito.spy(minioService);
        doReturn(false).when(spyService).objectExists(objectName);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.getSignedUrl(objectName));
        assertTrue(ex.getMessage().contains("Avatar object not found"));
    }

    @Test
    void getSignedUrl_Exception() throws Exception {
        String objectName = "test.txt";
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioProperties.getUrlExpiry()).thenReturn(Duration.ofSeconds(60));
        MinioServiceImpl spyService = Mockito.spy(minioService);
        doReturn(true).when(spyService).objectExists(objectName);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> spyService.getSignedUrl(objectName));
    }

    @Test
    void deleteFile_Success() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));
        assertDoesNotThrow(() -> minioService.deleteFile("test.txt"));
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_Exception() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        doThrow(new RuntimeException("fail")).when(minioClient).removeObject(any(RemoveObjectArgs.class));
        assertThrows(RuntimeException.class, () -> minioService.deleteFile("test.txt"));
    }

    @Test
    void objectExists_True() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(mock(StatObjectResponse.class));
        boolean exists = ReflectionTestUtils.invokeMethod(minioService, "objectExists", "test.txt");
        assertTrue(exists);
    }

    @Test
    void objectExists_False() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        ErrorResponseException ex = mock(ErrorResponseException.class);
        when(ex.errorResponse()).thenReturn(new io.minio.messages.ErrorResponse("NoSuchKey", null, null, null, null, null, null));
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(ex);
        boolean exists = ReflectionTestUtils.invokeMethod(minioService, "objectExists", "notfound.txt");
        assertFalse(exists);
    }

    @Test
    void objectExists_OtherException() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new RuntimeException("fail"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ReflectionTestUtils.invokeMethod(minioService, "objectExists", "error.txt"));
        assertTrue(ex.getMessage().contains("错误"));
    }
    @Test
    void objectExists_ErrorResponseException_OtherCode() throws Exception {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
        ErrorResponseException ex = mock(ErrorResponseException.class);
        io.minio.messages.ErrorResponse errorResponse = mock(io.minio.messages.ErrorResponse.class);
        when(errorResponse.code()).thenReturn("OtherError");
        when(ex.errorResponse()).thenReturn(errorResponse);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(ex);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(minioService, "objectExists", "error.txt"));
        assertTrue(thrown.getMessage().contains("错误"));
    }
}