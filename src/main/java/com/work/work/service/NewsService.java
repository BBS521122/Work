package com.work.work.service;

import com.work.work.entity.News;

import java.util.List;

public interface NewsService {
    void addNews(News news);
    List<News> getAllNews();
    void updateNews(News news);
    void deleteNews(Long id);
    News getNewsById(Long id);
    List<News> getDeletedNews();
    void restoreNews(Long id);
    void hardDeleteNews(Long id);
    void restoreNewsBatch(List<Long> ids);
    void hardDeleteNewsBatch(List<Long> ids);
    void deleteNewsBatch(List<Long> ids);
}
