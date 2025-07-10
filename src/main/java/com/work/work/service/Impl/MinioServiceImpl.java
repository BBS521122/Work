package com.work.work.service.Impl;

import com.work.work.service.MinioService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.work.work.properties.MinioProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {
    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;


    @Override
    public String uploadFile(MultipartFile file) throws RuntimeException {
        try {
            // 生成唯一对象名称
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + fileExtension;

            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectName)
                        .stream(inputStream, file.getSize(), minioProperties.getPartSize())
                        .contentType(file.getContentType())
                        .build());
            }

            return objectName;
        } catch (Exception e) {
            // 统一处理异常并抛出自定义异常
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }


    @Override
    public String uploadTextFile(File file,String fileName) throws RuntimeException {
        try {
            String originalFilename = file.getName();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID() + fileExtension;

            try (InputStream inputStream = new java.io.FileInputStream(file)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                        .stream(inputStream, file.length(), minioProperties.getPartSize())
                        .contentType("text/plain") // 如需识别类型可用 Files.probeContentType(file.toPath())
                        .build());
            }

            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSignedUrl(String objectName) {
        // 检查文件是否存在
        if (!objectExists(objectName)) {
            throw new RuntimeException("Avatar object not found");
        }
        // 生成预签名URL
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .expiry((int) minioProperties.getUrlExpiry().getSeconds())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean objectExists(String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectName)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            } else {
                throw new RuntimeException("错误: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("错误: " + e.getMessage(), e);
        }
    }

    /**
     * 获取File
     *
     * @param objectName
     * @return
     */
    @Override
    public File getFile(String objectName) {
        // 检查文件是否存在
        if (!objectExists(objectName)) {
            throw new RuntimeException("文件不存在");
        }
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("minio_", "_" + objectName);
            tempFile.deleteOnExit();
            // 下载对象到临时文件
            try (InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .build());
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (Exception e) {
            throw new RuntimeException("获取文件失败: " + e.getMessage(), e);
        }
    }
}
