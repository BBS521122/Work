package com.work.work.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    String uploadFile(MultipartFile file) throws RuntimeException;

    String getSignedAvatarUrl(String objectName);

    void deleteFile(String objectName);
}
