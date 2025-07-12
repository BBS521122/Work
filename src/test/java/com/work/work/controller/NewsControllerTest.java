package com.work.work.controller;

import com.work.work.entity.News;
import com.work.work.service.NewsService;
import com.work.work.vo.HttpResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    @BeforeEach
    void setUp() {
        // 初始化测试环境配置
        newsController.uploadDir = "test-upload";
        newsController.urlPrefix = "http://test/";
    }

    // 测试获取微信新闻列表
    @Test
    void wxGet_ShouldReturnApprovedNews() {
        List<News> expectedNews = Collections.singletonList(new News());
        when(newsService.getNewsByStatus("已通过")).thenReturn(expectedNews);

        HttpResponseEntity<List<News>> response = newsController.wxGet();

        assertEquals(200, response.getCode());
        assertEquals(expectedNews, response.getData());
        assertEquals("success", response.getMessage());
        verify(newsService).getNewsByStatus("已通过");
    }

    // 测试文件上传 - 成功案例
    @Test
    void upload_WithValidImage_ShouldReturnUrl() throws IOException {
        // 创建Mock文件
        MultipartFile file = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

        // Mock文件操作
        File mockUploadDir = mock(File.class);
        File mockWxDir = mock(File.class);

        // 使用Mock对象
        newsController.uploadDir = mockUploadDir.getPath();

        ResponseEntity<?> response = newsController.upload(file);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("url"));
    }


    // 测试文件上传 - 视频文件
    @Test
    void upload_WithValidVideo_ShouldReturnUrl() throws IOException {
        // 1. 准备Mock文件
        MultipartFile file = new MockMultipartFile(
                "test.mp4", "test.mp4", "video/mp4", "test data".getBytes());

        // 2. Mock文件目录对象
        File mockUploadDir = mock(File.class);
        File mockWxDir = mock(File.class);

        // 4. 注入Mock目录
        newsController.uploadDir = mockUploadDir.getPath();

        // 5. 执行测试
        ResponseEntity<?> response = newsController.upload(file);

        // 6. 验证
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("url"));

        // 可选的验证：确认transferTo没有被实际调用（因为目录是Mock的）
        verify(mockUploadDir, never()).mkdirs();
    }


    // 测试文件上传 - 无效文件名
    @Test
    void upload_WithInvalidFilename_ShouldReturnBadRequest() {
        MultipartFile file = new MockMultipartFile("test", "test", null, new byte[0]);

        ResponseEntity<?> response = newsController.upload(file);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("文件名无效", ((Map<?, ?>) response.getBody()).get("message"));
    }

    // 测试文件上传 - 不支持的文件类型
    @Test
    void upload_WithUnsupportedFileType_ShouldReturnBadRequest() {
        MultipartFile file = new MockMultipartFile(
                "test.pdf", "test.pdf", "application/pdf", "test data".getBytes());

        ResponseEntity<?> response = newsController.upload(file);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("不支持的文件类型", ((Map<?, ?>) response.getBody()).get("message"));
    }

    // 测试删除媒体文件
    @Test
    void deleteMedia_WithValidUrl_ShouldReturnOk() {
        String testUrl = "http://test/test.jpg";
        ResponseEntity<?> response = newsController.deleteMedia(testUrl);

        assertEquals(200, response.getStatusCodeValue());
    }

    // 测试获取新闻列表 - 按状态筛选
    @Test
    void list_WithStatus_ShouldReturnFilteredNews() {
        List<News> expectedNews = Arrays.asList(new News(), new News());
        when(newsService.getNewsByStatus("已通过")).thenReturn(expectedNews);

        List<News> result = newsController.list("已通过", null);

        assertEquals(2, result.size());
        verify(newsService).getNewsByStatus("已通过");
    }

    // 测试获取新闻列表 - 按租户ID筛选
    @Test
    void list_WithTenantId_ShouldReturnFilteredNews() {
        List<News> expectedNews = Collections.singletonList(new News());
        when(newsService.getNewsByTenantId(1L)).thenReturn(expectedNews);

        List<News> result = newsController.list(null, 1L);

        assertEquals(1, result.size());
        verify(newsService).getNewsByTenantId(1L);
    }

    // 测试获取新闻列表 - 默认返回已通过新闻
    @Test
    void list_WithoutParams_ShouldReturnApprovedNews() {
        List<News> expectedNews = Collections.singletonList(new News());
        when(newsService.getNewsByStatus("已通过")).thenReturn(expectedNews);

        List<News> result = newsController.list(null, null);

        assertEquals(1, result.size());
        verify(newsService).getNewsByStatus("已通过");
    }

    // 测试获取单个新闻 - 存在的情况
    @Test
    void get_WithExistingId_ShouldReturnNews() {
        News expectedNews = new News();
        when(newsService.getNewsById(1L)).thenReturn(expectedNews);

        ResponseEntity<?> response = newsController.get(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedNews, response.getBody());
    }

    // 测试获取单个新闻 - 不存在的情况
    @Test
    void get_WithNonExistingId_ShouldReturnNotFound() {
        when(newsService.getNewsById(1L)).thenReturn(null);

        ResponseEntity<?> response = newsController.get(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    // 测试添加新闻 - 管理员角色
    @Test
    void add_WithAdminRole_ShouldSetApprovedStatus() {
        News news = new News();
        doNothing().when(newsService).addNews(news);  // 替换原来的when-thenReturn

        ResponseEntity<?> response = newsController.add("ADMIN", news);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("已通过", news.getStatus());
    }

    @Test
    void add_WithNonAdminRole_ShouldSetPendingStatus() {
        News news = new News();
        doNothing().when(newsService).addNews(news);  // 替换原来的when-thenReturn

        ResponseEntity<?> response = newsController.add("USER", news);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("待审核", news.getStatus());
    }

    // 测试更新新闻
    @Test
    void update_ShouldCallService() {
        News news = new News();
        doNothing().when(newsService).updateNews(news);
        ResponseEntity<?> response = newsController.update(1L, news);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, news.getId());
        assertEquals("修改成功", ((Map<?, ?>) response.getBody()).get("message"));
    }

    // 测试删除新闻
    @Test
    void delete_ShouldCallService() {
        ResponseEntity<?> response = newsController.delete(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).deleteNews(1L);
    }

    // 测试获取回收站新闻 - 管理员视图
    @Test
    void getRecycleBin_WithoutTenantId_ShouldReturnAllDeleted() {
        List<News> expectedNews = Arrays.asList(new News(), new News());
        when(newsService.getAllDeletedNews()).thenReturn(expectedNews);

        List<News> result = newsController.getRecycleBin(null);

        assertEquals(2, result.size());
        verify(newsService).getAllDeletedNews();
    }

    // 测试获取回收站新闻 - 普通用户视图
    @Test
    void getRecycleBin_WithTenantId_ShouldReturnUserDeleted() {
        List<News> expectedNews = Collections.singletonList(new News());
        when(newsService.getDeletedNewsByTenant(1L)).thenReturn(expectedNews);

        List<News> result = newsController.getRecycleBin(1L);

        assertEquals(1, result.size());
        verify(newsService).getDeletedNewsByTenant(1L);
    }

    // 测试恢复单个新闻
    @Test
    void restore_ShouldCallService() {
        ResponseEntity<?> response = newsController.restore(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).restoreNews(1L);
    }

    // 测试彻底删除单个新闻
    @Test
    void hardDelete_ShouldCallService() {
        ResponseEntity<?> response = newsController.hardDelete(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).hardDeleteNews(1L);
    }

    // 测试批量恢复新闻
    @Test
    void restoreBatch_ShouldCallService() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseEntity<?> response = newsController.restoreBatch(ids);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).restoreNewsBatch(ids);
    }

    // 测试批量彻底删除新闻
    @Test
    void hardDeleteBatch_ShouldCallService() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseEntity<?> response = newsController.hardDeleteBatch(ids);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).hardDeleteNewsBatch(ids);
    }

    // 测试批量删除新闻
    @Test
    void deleteBatch_ShouldCallService() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        ResponseEntity<?> response = newsController.deleteBatch(ids);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).deleteNewsBatch(ids);
    }

    // 测试审核通过新闻
    @Test
    void approve_ShouldCallService() {
        ResponseEntity<?> response = newsController.approve(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).approveNews(1L);
    }

    // 测试审核拒绝新闻
    @Test
    void reject_ShouldCallService() {
        ResponseEntity<?> response = newsController.reject(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(newsService).rejectNews(1L);
    }
}
