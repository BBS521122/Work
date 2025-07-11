package com.work.work.service;

public interface AliCloudService {
    void uploadFile(String name);
    String getUrl(String name);
    String submitTrans(String url);
    String getTrans(String taskId);
}
