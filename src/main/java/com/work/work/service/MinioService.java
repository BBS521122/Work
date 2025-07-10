package com.work.work.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface MinioService {
    String uploadFile(MultipartFile file) throws RuntimeException;
    String uploadTextFile(File file, String fileName) throws RuntimeException;

    String getSignedUrl(String objectName);

    void deleteFile(String objectName);
    File getFile(String objectName);
}
