package com.work.work.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {
    String uploadFile(MultipartFile file) throws RuntimeException;

    String getSignedUrl(String objectName);

    InputStream getFile(String objectName);

    void deleteFile(String objectName);
}