package com.work.work.service;

import com.work.work.entity.News;

import java.util.List;

public interface NewsService {
    void addNews(News news);
    List<News> getNewsByStatus(String status);
    void updateNews(News news);
    void deleteNews(Long id);
    News getNewsById(Long id);
    List<News> getAllDeletedNews();
    List<News> getDeletedNewsByTenant(Long tenantId);
    void restoreNews(Long id);
    void hardDeleteNews(Long id);
    void restoreNewsBatch(List<Long> ids);
    void hardDeleteNewsBatch(List<Long> ids);
    void deleteNewsBatch(List<Long> ids);
    void approveNews(Long id);
    void rejectNews(Long id);
    List<News> getNewsByTenantId(Long tenantId);

}