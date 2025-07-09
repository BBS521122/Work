package com.work.work.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseService {

    @Autowired
    MinioService minioService;

    public String updateCourse( MultipartFile file) {
        String newName = null;
        try {
            // 1. 获取旧头像名称

            // 2. 上传新头像
            newName = minioService.uploadFile(file);
            return newName;
        } catch (Exception e) {
            // 如果上传新头像后出现异常，尝试删除新头像
            if (newName != null) {
                try {
                    minioService.deleteFile(newName);
                } catch (Exception ex) {
                    // 记录日志，忽略异常
                }
            }
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}
