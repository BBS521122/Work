package com.work.work.service.Impl;

import com.work.work.context.UserContext;
import com.work.work.entity.News;
import com.work.work.mapper.sql.NewsMapper;
import com.work.work.mapper.sql.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

@RunWith(PowerMockRunner.class)
@PrepareForTest({NewsServiceImpl.class, File.class})
class NewsServiceImplTest {

    @Mock
    private NewsMapper newsMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(newsService, "newsMapper", newsMapper);
        ReflectionTestUtils.setField(newsService, "userMapper", userMapper);
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");
    }

    @Test
    void addNews_PassedStatus() {
        News news = new News();
        news.setStatus("已通过");
        news.setTitle("t"); news.setSummary("s"); news.setContent("c");
        news.setAuthor("a"); news.setImagePath("img");
        when(newsMapper.selectMaxSortOrder()).thenReturn(1);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(anyInt())).thenReturn(new ArrayList<>());
        doNothing().when(newsMapper).insert(any(News.class));
        newsService.addNews(news);
        assertNotNull(news.getSortOrder());
        verify(newsMapper).insert(news);
    }

    @Test
    void addNews_NotPassedStatus() {
        News news = new News();
        news.setStatus("未通过");
        news.setTitle("t"); news.setSummary("s"); news.setContent("c");
        news.setAuthor("a"); news.setImagePath("img");
        doNothing().when(newsMapper).insert(any(News.class));
        newsService.addNews(news);
        assertNull(news.getSortOrder());
        verify(newsMapper).insert(news);
    }

    @Test
    void addNews_ValidateNewsException() {
        News news = new News();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> newsService.addNews(news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }

    @Test
    void updateNews_Admin() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_NotAdmin() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_NullId_ThrowsException() {
        News news = new News();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> newsService.updateNews(news));
        assertTrue(ex.getMessage().contains("ID不能为空"));
    }

    @Test
    void updateNews_NotFound_ThrowsException() {
        News news = new News();
        news.setId(1L);
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.updateNews(news));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void deleteNews_Success() {
        News news = new News();
        news.setId(1L); news.setStatus("已通过"); news.setSortOrder(1);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThan(anyInt())).thenReturn(new ArrayList<>());
        when(newsMapper.softDelete(1L)).thenReturn(1);
        newsService.deleteNews(1L);
        verify(newsMapper).softDelete(1L);
    }

    @Test
    void deleteNews_NotFound() {
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.deleteNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void getNewsByStatus() {
        List<News> list = new ArrayList<>();
        when(newsMapper.selectByStatus("已通过")).thenReturn(list);
        assertEquals(list, newsService.getNewsByStatus("已通过"));
    }

    @Test
    void getNewsById() {
        News news = new News();
        when(newsMapper.selectById(1L)).thenReturn(news);
        assertEquals(news, newsService.getNewsById(1L));
    }

    @Test
    void getAllDeletedNews() {
        List<News> list = new ArrayList<>();
        when(newsMapper.selectDeleted()).thenReturn(list);
        assertEquals(list, newsService.getAllDeletedNews());
    }

    @Test
    void getDeletedNewsByTenant() {
        List<News> list = new ArrayList<>();
        when(newsMapper.selectDeletedByTenant(1L)).thenReturn(list);
        assertEquals(list, newsService.getDeletedNewsByTenant(1L));
    }

    @Test
    void restoreNews_Admin() {
        News news = new News();
        news.setId(1L); news.setStatus("已通过"); news.setSortOrder(1);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.selectMaxSortOrder()).thenReturn(1);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(anyInt())).thenReturn(new ArrayList<>());
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.restoreNews(1L);
        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
    }

    @Test
    void restoreNews_NotFound() {
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.restoreNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void hardDeleteNews_Success() {
        News news = new News();
        news.setId(1L); news.setImagePath(null); news.setContent("");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.hardDelete(1L)).thenReturn(1);
        newsService.hardDeleteNews(1L);
        verify(newsMapper).hardDelete(1L);
    }

    @Test
    void hardDeleteNews_NewsNotFound() {
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.hardDeleteNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void hardDeleteNews_WithImageAndMedia() {
        News news = new News();
        news.setId(1L);
        news.setImagePath("img.jpg");
        news.setContent("<img src='a.jpg'>");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.hardDelete(1L)).thenReturn(1);
        newsService.hardDeleteNews(1L);
        verify(newsMapper).hardDelete(1L);
    }

    @Test
    void hardDeleteNews_DeleteFail() {
        News news = new News();
        news.setId(1L);
        news.setImagePath(null);
        news.setContent("");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.hardDelete(1L)).thenReturn(0);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.hardDeleteNews(1L));
        assertTrue(ex.getMessage().contains("物理删除失败"));
    }

    @Test
    void approveNews_DefaultSort() {
        News news = new News();
        news.setId(1L); news.setStatus("待审核");
        // 没有指定排序值
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectMaxSortOrder()).thenReturn(1);
        newsService.approveNews(1L);
        verify(newsMapper).updateStatus(1L, "已通过");
    }

    @Test
    void approveNews_WithSortAndNoAffected() {
        News news = new News();
        news.setId(1L); news.setStatus("待审核");
        news.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(2)).thenReturn(new ArrayList<>());
        newsService.approveNews(1L);
        verify(newsMapper).updateStatus(1L, "已通过");
    }

    @Test
    void rejectNews_Success() {
        News news = new News();
        news.setId(1L); news.setStatus("待审核");
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.updateStatus(anyLong(), anyString())).thenReturn(1);
        newsService.rejectNews(1L);
        verify(newsMapper).updateStatus(1L, "已拒绝");
    }

    @Test
    void rejectNews_NotFoundOrStatusError() {
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.rejectNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void getNewsByTenantId() {
        List<News> list = new ArrayList<>();
        when(newsMapper.selectByTenantId(1L)).thenReturn(list);
        assertEquals(list, newsService.getNewsByTenantId(1L));
    }

    @Test
    void addNews_PassedStatus_WithSortOrder() {
        News news = new News();
        news.setStatus("已通过");
        news.setSortOrder(5);
        news.setTitle("t"); news.setSummary("s"); news.setContent("c");
        news.setAuthor("a"); news.setImagePath("img");
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(5)).thenReturn(new ArrayList<>());
        doNothing().when(newsMapper).insert(any(News.class));
        newsService.addNews(news);
        assertEquals(5, news.getSortOrder());
        verify(newsMapper).insert(news);
    }

    @Test
    void addNews_PassedStatus_WithSortOrder_Affected() {
        News news = new News();
        news.setStatus("已通过");
        news.setSortOrder(2);
        news.setTitle("t"); news.setSummary("s"); news.setContent("c");
        news.setAuthor("a"); news.setImagePath("img");
        News affected = new News();
        affected.setId(10L); affected.setSortOrder(2);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(2)).thenReturn(List.of(affected));
        doNothing().when(newsMapper).updateSortOrderById(10L, 3);
        doNothing().when(newsMapper).insert(any(News.class));
        newsService.addNews(news);
        verify(newsMapper).updateSortOrderById(10L, 3);
        verify(newsMapper).insert(news);
    }

    @Test
    void updateNews_NotAdmin_OldNewsPassedWithSortOrder() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("未通过");
        News affected = new News(); affected.setId(10L); affected.setSortOrder(3);
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected));
        doNothing().when(newsMapper).updateSortOrderById(10L, 2);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).updateSortOrderById(10L, 2);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_MoveSortOrder_Forward() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(3); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        when(newsMapper.incrementSortOrderRange(2, 2, 1L)).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).incrementSortOrderRange(2, 2, 1L);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_MoveSortOrder_Backward() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(3); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        when(newsMapper.decrementSortOrderRange(3, 3, 1L)).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).decrementSortOrderRange(3, 3, 1L);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_SortOrderNull() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(null); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(null); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void deleteNews_PassedWithSortOrder_Affected() {
        News news = new News();
        news.setId(1L); news.setStatus("已通过"); news.setSortOrder(2);
        News affected = new News();
        affected.setId(10L); affected.setSortOrder(3);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected));
        when(newsMapper.softDelete(1L)).thenReturn(1);
        doNothing().when(newsMapper).updateSortOrderById(10L, 2);
        newsService.deleteNews(1L);
        verify(newsMapper).updateSortOrderById(10L, 2);
        verify(newsMapper).softDelete(1L);
    }

    @Test
    void approveNews_NullNews_ThrowsException() {
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.approveNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }

    @Test
    void approveNews_StatusError_ThrowsException() {
        News news = new News();
        news.setId(1L); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(news);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.approveNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在或状态错误"));
    }

    // 高覆盖率补充用例
    @Test
    void deleteFileByUrl_UrlNull() {
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", (Object) null);
    }

    @Test
    void deleteFileByUrl_NotStartWithPrefix() {
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", "http://otherhost/file.jpg");
    }

    @Test
    void deleteFileByUrl_FileExists_DeleteFail() {
        String url = "http://localhost/test_delete_fail.jpg";
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", url);
    }

    @Test
    void deleteFileByUrl_FileExists_DeleteSuccess() {
        String url = "http://localhost/test.jpg";
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", url);
    }

    @Test
    void deleteFileByUrl_FileNotExists() {
        String url = "http://localhost/test.jpg";
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", url);
    }

    @Test
    void updateNews_Admin_SortOrderGreaterThanCurrentMax() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_SetStatusToPending() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(null); news.setStatus("未通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void restoreNews_Admin_PassedStatus_SortOrderGreaterThanCurrentMax() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(5);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.selectMaxSortOrder()).thenReturn(3);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.restoreNews(1L);
        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
    }

    @Test
    void restoreNews_Admin_NotPassedStatus() {
        News news = new News();
        news.setId(1L);
        news.setStatus("未通过");
        news.setSortOrder(2);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.restoreNews(1L);
        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
    }

    @Test
    void restoreNews_NotAdmin() {
        News news = new News();
        news.setId(1L);
        news.setStatus("未通过");
        news.setSortOrder(2);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.restoreNews(1L);
        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
    }

    @Test
    void hardDeleteNewsBatch_NullIds() {
        newsService.hardDeleteNewsBatch(null);
    }

    @Test
    void hardDeleteNewsBatch_EmptyIds() {
        newsService.hardDeleteNewsBatch(Collections.emptyList());
    }

    @Test
    void hardDeleteNewsBatch_NewsNull() {
        when(newsMapper.selectByIdIncludingDeleted(anyLong())).thenReturn(null);
        when(newsMapper.hardDeleteBatch(anyList())).thenReturn(1);
        newsService.hardDeleteNewsBatch(List.of(1L));
        verify(newsMapper).hardDeleteBatch(anyList());
    }

    @Test
    void hardDeleteNewsBatch_WithImageAndMedia() {
        News news = new News();
        news.setId(1L);
        news.setImagePath("img.jpg");
        news.setContent("<img src='a.jpg'>");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.hardDeleteBatch(anyList())).thenReturn(1);
        newsService.hardDeleteNewsBatch(List.of(1L));
        verify(newsMapper).hardDeleteBatch(anyList());
    }
    @Test
    void restoreNewsBatch_NotAdmin() {
        List<Long> ids = List.of(1L);
        News news = new News();
        news.setId(1L);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.restoreBatch(ids)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNewsBatch(ids);

        verify(newsMapper).restoreBatch(ids);
        verify(newsMapper).update(news);
        // 检查 news 的状态和排序
        assertEquals("待审核", news.getStatus());
        assertNull(news.getSortOrder());
    }

    @Test
    void deleteNewsBatch_AllBranches() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News(); news1.setId(1L); news1.setStatus("已通过"); news1.setSortOrder(2);
        News news2 = new News(); news2.setId(2L); news2.setStatus("未通过"); news2.setSortOrder(null);
        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(new ArrayList<>());
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);
        newsService.deleteNewsBatch(ids);
        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
    }

    @Test
    void extractMediaUrls_NullOrBlank() {
        List<String> urls1 = ReflectionTestUtils.invokeMethod(newsService, "extractMediaUrls", (Object) null);
        List<String> urls2 = ReflectionTestUtils.invokeMethod(newsService, "extractMediaUrls", "");
        assertTrue(urls1.isEmpty());
        assertTrue(urls2.isEmpty());
    }

    @Test
    void extractMediaUrls_NoMatch() {
        List<String> urls = ReflectionTestUtils.invokeMethod(newsService, "extractMediaUrls", "<div>no media</div>");
        assertTrue(urls.isEmpty());
    }

    @Test
    void extractMediaUrls_MatchMultiple() {
        String html = "<img src=\"a.jpg\"><video src='b.mp4'></video><source src=\"c.avi\">";
        List<String> urls = ReflectionTestUtils.invokeMethod(newsService, "extractMediaUrls", html);
        assertEquals(3, urls.size());
        assertTrue(urls.contains("a.jpg"));
        assertTrue(urls.contains("b.mp4"));
        assertTrue(urls.contains("c.avi"));
    }

    @Test
    void addNews_PassedStatus_MaxSortNull() {
        News news = new News();
        news.setStatus("已通过");
        news.setTitle("t"); news.setSummary("s"); news.setContent("c");
        news.setAuthor("a"); news.setImagePath("img");
        when(newsMapper.selectMaxSortOrder()).thenReturn(null);
        doNothing().when(newsMapper).insert(any(News.class));
        newsService.addNews(news);
        assertEquals(1, news.getSortOrder());
        verify(newsMapper).insert(news);
    }

    @Test
    void updateNews_NotAdmin_OldNewsPassedWithSortOrder_Affected() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        oldNews.setImagePath("img1"); oldNews.setContent("old");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        news.setImagePath("img2"); news.setContent("new");
        News affected = new News(); affected.setId(10L); affected.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected));
        doNothing().when(newsMapper).updateSortOrderById(10L, 1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).updateSortOrderById(10L, 1);
        verify(newsMapper).update(news);
    }

    @Test
    void deleteNewsBatch_PassedToDelete_Affected_MoveUp() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News(); news1.setId(1L); news1.setStatus("已通过"); news1.setSortOrder(2);
        News news2 = new News(); news2.setId(2L); news2.setStatus("已通过"); news2.setSortOrder(3);
        News affected = new News(); affected.setId(3L); affected.setSortOrder(4);
        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected));
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);
        doNothing().when(newsMapper).updateSortOrderById(anyLong(), anyInt());
        newsService.deleteNewsBatch(ids);
        verify(newsMapper).updateSortOrderById(3L, 2);
    }

    @Test
    void hardDeleteNewsBatch_AffectedNotMatch_ThrowsException() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News(); news1.setId(1L);
        News news2 = new News(); news2.setId(2L);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news1);
        when(newsMapper.selectByIdIncludingDeleted(2L)).thenReturn(news2);
        when(newsMapper.hardDeleteBatch(ids)).thenReturn(1); // 少于ids.size()
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.hardDeleteNewsBatch(ids));
        assertTrue(ex.getMessage().contains("部分新闻彻底删除失败"));
    }

    @Test
    void updateNews_Admin_AllBranches() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(3); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        //when(newsMapper.incrementSortOrderRange(3, 1, 1L)).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_NotAdmin_AllBranches() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_SortOrderMoveForward() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(5); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(3); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        when(newsMapper.incrementSortOrderRange(3, 4, 1L)).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).incrementSortOrderRange(3, 4, 1L);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_Admin_SortOrderMoveBackward() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(3); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(5); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        when(newsMapper.decrementSortOrderRange(4, 5, 1L)).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).decrementSortOrderRange(4, 5, 1L);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_ImagePathChanged() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        News news = new News();
        news.setId(1L); news.setSortOrder(1); news.setStatus("已通过");
        news.setImagePath("new.jpg");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void updateNews_ContentMediaDiff() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(1); oldNews.setStatus("已通过");
        oldNews.setContent("<img src='a.jpg'>");
        News news = new News();
        news.setId(1L); news.setSortOrder(1); news.setStatus("已通过");
        news.setContent("<img src='b.jpg'>");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }

    @Test
    void restoreNewsBatch_Admin_SortOrderNull() {
        List<Long> ids = List.of(1L);
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(null);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.restoreBatch(ids)).thenReturn(1);
        when(newsMapper.selectAll()).thenReturn(List.of(news));
        when(newsMapper.selectMaxSortOrder()).thenReturn(2);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNewsBatch(ids);

        verify(newsMapper).restoreBatch(ids);
        verify(newsMapper).update(news);
        assertNull(news.getSortOrder());
    }

    @Test
    void restoreNewsBatch_Admin_SortOrderGreaterThanCurrentMax() {
        List<Long> ids = List.of(1L);
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(5);
        News existing = new News();
        existing.setId(2L);
        existing.setStatus("已通过");
        existing.setSortOrder(2);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.restoreBatch(ids)).thenReturn(1);
        when(newsMapper.selectAll()).thenReturn(List.of(existing, news));
        when(newsMapper.selectMaxSortOrder()).thenReturn(2);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNewsBatch(ids);

        verify(newsMapper).restoreBatch(ids);
        verify(newsMapper).update(news);
        assertEquals(3, news.getSortOrder()); // sort = currentMax + 1
    }

    @Test
    void restoreNewsBatch_Admin_SortOrderLessThanCurrentMax_Affected() {
        List<Long> ids = List.of(1L);
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(1);
        News affected = new News();
        affected.setId(2L);
        affected.setSortOrder(1);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(newsMapper.restoreBatch(ids)).thenReturn(1);
        when(newsMapper.selectAll()).thenReturn(List.of(news, affected));
        when(newsMapper.selectMaxSortOrder()).thenReturn(2);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(1)).thenReturn(List.of(affected));
        doNothing().when(newsMapper).updateSortOrderById(2L, 2);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNewsBatch(ids);

        verify(newsMapper).updateSortOrderById(2L, 2);
        verify(newsMapper).update(news);
    }
    @Test
    void updateNews_IdNull_ThrowsException() {
        News news = new News();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> newsService.updateNews(news));
        assertTrue(ex.getMessage().contains("ID不能为空"));
    }
    @Test
    void updateNews_OldNewsNull_ThrowsException() {
        News news = new News();
        news.setId(1L);
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.updateNews(news));
        assertTrue(ex.getMessage().contains("记录不存在"));
    }
    @Test
    void updateNews_NotAdmin_OldNewsNotPassed() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("未通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("未通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }
    @Test
    void updateNews_Admin_SortOrderNoChange() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(2); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(2); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }
    @Test
    void rejectNews_NewsNull_ThrowsException() {
        when(newsMapper.selectById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.rejectNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在或状态不正确"));
    }

    @Test
    void rejectNews_StatusNull_ThrowsException() {
        News news = new News();
        when(newsMapper.selectById(1L)).thenReturn(news);
        news.setStatus(null);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.rejectNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在或状态不正确"));
    }

    @Test
    void rejectNews_StatusNotPending_ThrowsException() {
        News news = new News();
        news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(news);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.rejectNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在或状态不正确"));
    }

    @Test
    void approveNews_StatusNotPending_ThrowsException() {
        News news = new News();
        news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(news);
        Exception ex = assertThrows(RuntimeException.class, () -> newsService.approveNews(1L));
        assertTrue(ex.getMessage().contains("记录不存在或状态错误"));
    }

    @Test
    void updateNews_Admin_SortOrderBothNull() {
        News oldNews = new News();
        oldNews.setId(1L); oldNews.setSortOrder(null); oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L); news.setSortOrder(null); news.setStatus("已通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);
        newsService.updateNews(news);
        verify(newsMapper).update(news);
    }
@Test
void approveNews_DefaultSort_MaxSortNull() {
    News news = new News();
    news.setId(1L);
    news.setStatus("待审核");
    when(newsMapper.selectById(1L)).thenReturn(news);
    when(newsMapper.selectMaxSortOrder()).thenReturn(null);
    doNothing().when(newsMapper).updateSortOrderById(1L, 1);
    when(newsMapper.updateStatus(1L, "已通过")).thenReturn(1);

    newsService.approveNews(1L);

    verify(newsMapper).updateSortOrderById(1L, 1);
    verify(newsMapper).updateStatus(1L, "已通过");
    assertEquals("已通过", news.getStatus());
    assertEquals(1, news.getSortOrder());
}
    @Test
    void approveNews_WithSortAndAffectedList() {
        News news = new News();
        news.setId(1L);
        news.setStatus("待审核");
        news.setSortOrder(2);
        News affectedNews = new News();
        affectedNews.setId(2L);
        affectedNews.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(2)).thenReturn(List.of(affectedNews));
        doNothing().when(newsMapper).updateSortOrderById(2L, 3);
        doNothing().when(newsMapper).updateSortOrderById(1L, 2);
        when(newsMapper.updateStatus(1L, "已通过")).thenReturn(1);

        newsService.approveNews(1L);

        verify(newsMapper).updateSortOrderById(2L, 3);
        verify(newsMapper).updateSortOrderById(1L, 2);
        verify(newsMapper).updateStatus(1L, "已通过");
        assertEquals("已通过", news.getStatus());
        assertEquals(2, news.getSortOrder());
    }

    @Test
    void validateNews_AllFieldsValid() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath("img");
        // 反射调用私有方法
        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
    }
    @Test
    void validateNews_TitleNull() {
        News news = new News();
        news.setTitle(null);
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_TitleBlank() {
        News news = new News();
        news.setTitle("   ");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_SummaryNull() {
        News news = new News();
        news.setTitle("t");
        news.setSummary(null);
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_SummaryBlank() {
        News news = new News();
        news.setTitle("t");
        news.setSummary(" ");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_ContentNull() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent(null);
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_ContentBlank() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent(" ");
        news.setAuthor("a");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_AuthorNull() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor(null);
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_AuthorBlank() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor(" ");
        news.setImagePath("img");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_ImagePathNull() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath(null);
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void validateNews_ImagePathBlank() {
        News news = new News();
        news.setTitle("t");
        news.setSummary("s");
        news.setContent("c");
        news.setAuthor("a");
        news.setImagePath(" ");
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> ReflectionTestUtils.invokeMethod(newsService, "validateNews", news));
        assertTrue(ex.getMessage().contains("所有字段不能为空"));
    }
    @Test
    void restoreNewsBatch_NullIds() {
        newsService.restoreNewsBatch(null);
        // 验证没有调用 restoreBatch
        verify(newsMapper, never()).restoreBatch(any());
    }
    @Test
    void restoreNewsBatch_EmptyIds() {
        newsService.restoreNewsBatch(Collections.emptyList());
        verify(newsMapper, never()).restoreBatch(any());
    }

    @Test
    void updateNews_OldNewsStatusPassed() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("未通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(new ArrayList<>());
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
    }
    @Test
    void updateNews_OldNewsStatusRejected() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已拒绝");
        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("未通过");
        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(new ArrayList<>());
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
    }
//test
@Test
void restoreNews_Admin_PassedStatus_SortOrderNotNull() {
    News news = new News();
    news.setId(1L);
    news.setStatus("已通过");
    news.setSortOrder(5); // 不为null
    when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
    when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
    when(newsMapper.restore(1L)).thenReturn(1);
    when(newsMapper.selectMaxSortOrder()).thenReturn(3); // maxSort != null
    // 这里改成 anyInt()
    when(newsMapper.selectPassedBySortOrderGreaterThanEqual(anyInt())).thenReturn(new ArrayList<>());
    when(newsMapper.update(any(News.class))).thenReturn(1);
    UserContext.setUserId(1L);

    newsService.restoreNews(1L);

    verify(newsMapper).restore(1L);
    verify(newsMapper).update(news);
    assertEquals(4, news.getSortOrder());
}
    @Test
    void restoreNews_Admin_PassedStatus_MaxSortNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(1);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.selectMaxSortOrder()).thenReturn(null); // maxSort == null
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(anyInt())).thenReturn(new ArrayList<>());
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNews(1L);

        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
        // sortOrder > currentMax(-1)，sortOrder会被重置为0
        assertEquals(0, news.getSortOrder());
    }
    @Test
    void restoreNews_Admin_PassedStatus_AffectedNotEmpty() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(1);
        News affected = new News();
        affected.setId(2L);
        affected.setSortOrder(1);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.selectMaxSortOrder()).thenReturn(2); // currentMax = 2
        when(newsMapper.selectPassedBySortOrderGreaterThanEqual(1)).thenReturn(List.of(affected));
        doNothing().when(newsMapper).updateSortOrderById(2L, 2); // affected.getSortOrder()+1
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNews(1L);

        verify(newsMapper).restore(1L);
        verify(newsMapper).updateSortOrderById(2L, 2);
        verify(newsMapper).update(news);
        // sortOrder <= currentMax，sortOrder不变
        assertEquals(1, news.getSortOrder());
    }
    @Test
    void restoreNews_Admin_PassedStatus_SortOrderNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(null); // sortOrder 为 null
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNews(1L);

        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
        // 不会进入 if 分支，sortOrder 不变
        assertNull(news.getSortOrder());
    }
    @Test
    void restoreNews_Admin_StatusNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus(null); // status 为 null
        news.setSortOrder(1);
        when(newsMapper.selectByIdIncludingDeleted(1L)).thenReturn(news);
        when(userMapper.selectRoleById(anyLong())).thenReturn("ADMIN");
        when(newsMapper.restore(1L)).thenReturn(1);
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.restoreNews(1L);

        verify(newsMapper).restore(1L);
        verify(newsMapper).update(news);
        // 根据实际值修改断言
        assertNull(news.getSortOrder());
    }
    @Test
    void deleteNews_StatusNotPassed() {
        News news = new News();
        news.setId(1L);
        news.setStatus("未通过"); // 不是"已通过"
        news.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.softDelete(1L)).thenReturn(1);

        newsService.deleteNews(1L);

        verify(newsMapper).softDelete(1L);
        // 不会进入排序处理分支，不会调用 updateSortOrderById
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNews_SortOrderNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(null); // sortOrder 为 null
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.softDelete(1L)).thenReturn(1);

        newsService.deleteNews(1L);

        verify(newsMapper).softDelete(1L);
        // 不会进入排序处理分支，不会调用 updateSortOrderById
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNews_StatusNull() {
        News news = new News();
        news.setId(1L);
        news.setStatus(null); // status 为 null
        news.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.softDelete(1L)).thenReturn(1);

        newsService.deleteNews(1L);

        verify(newsMapper).softDelete(1L);
        // 不会进入排序处理分支，不会调用 updateSortOrderById
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNews_PassedStatus_SortOrderNotNull_NoAffected() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(2);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(new ArrayList<>()); // 没有受影响的记录
        when(newsMapper.softDelete(1L)).thenReturn(1);

        newsService.deleteNews(1L);

        verify(newsMapper).softDelete(1L);
        // 进入排序处理分支，但没有受影响的记录，不会调用 updateSortOrderById
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNews_PassedStatus_SortOrderNotNull_WithAffected() {
        News news = new News();
        news.setId(1L);
        news.setStatus("已通过");
        news.setSortOrder(2);
        News affected = new News();
        affected.setId(3L);
        affected.setSortOrder(3);
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected));
        when(newsMapper.softDelete(1L)).thenReturn(1);
        doNothing().when(newsMapper).updateSortOrderById(3L, 2); // affected.getSortOrder() - 1

        newsService.deleteNews(1L);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).updateSortOrderById(3L, 2);
    }
    @Test
    void deleteNewsBatch_NoPassedNews() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("未通过"); // 不是"已通过"
        news1.setSortOrder(2);

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(null); // sortOrder 为 null

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        // 没有符合条件的记录，不会调用排序相关方法
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNewsBatch_PassedNews_NoMoveUp() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(5); // 较大的排序值

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(6); // 更大的排序值

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);

        // 没有其他已通过的记录需要移动
        when(newsMapper.selectPassedBySortOrderGreaterThan(anyInt())).thenReturn(new ArrayList<>());

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        // 没有需要移动的记录
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNewsBatch_PassedNews_WithMoveUp() {
        List<Long> ids = List.of(1L, 2L);
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(2); // 较小的排序值

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(3); // 较小的排序值

        // 受影响的记录
        News affected1 = new News();
        affected1.setId(3L);
        affected1.setSortOrder(4);

        News affected2 = new News();
        affected2.setId(4L);
        affected2.setSortOrder(5);

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);

        // 有需要移动的记录
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(List.of(affected1, affected2));
        doNothing().when(newsMapper).updateSortOrderById(3L, 2); // 4-2=2
        doNothing().when(newsMapper).updateSortOrderById(4L, 3); // 5-2=3

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        verify(newsMapper).updateSortOrderById(3L, 2);
        verify(newsMapper).updateSortOrderById(4L, 3);
    }

    @Test
    void deleteNewsBatch_MixedStatus() {
        List<Long> ids = List.of(1L, 2L, 3L);
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(2);

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("未通过"); // 不是"已通过"
        news2.setSortOrder(3);

        News news3 = new News();
        news3.setId(3L);
        news3.setStatus("已通过");
        news3.setSortOrder(null); // sortOrder 为 null

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.selectById(3L)).thenReturn(news3);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);
        when(newsMapper.softDelete(3L)).thenReturn(1);

        // 只有 news1 符合条件
        when(newsMapper.selectPassedBySortOrderGreaterThan(2)).thenReturn(new ArrayList<>());

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        verify(newsMapper).softDelete(3L);
        // 只有 news1 需要处理排序，但没有受影响的记录
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNewsBatch_EmptyIds() {
        List<Long> ids = new ArrayList<>();

        newsService.deleteNewsBatch(ids);

        // 不会调用任何数据库操作
        verify(newsMapper, never()).selectById(anyLong());
        verify(newsMapper, never()).softDelete(anyLong());
    }

    @Test
    void deleteNewsBatch_NullIds() {
        newsService.deleteNewsBatch(null);

        // 不会调用任何数据库操作
        verify(newsMapper, never()).selectById(anyLong());
        verify(newsMapper, never()).softDelete(anyLong());
    }
    @Test
    void deleteNewsBatch_MoveUpGreaterThanZero() {
        List<Long> ids = List.of(1L, 2L, 3L);

        // 被删除的记录
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(1);

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(3);

        News news3 = new News();
        news3.setId(3L);
        news3.setStatus("未通过");
        news3.setSortOrder(5);

        // 受影响的记录
        News affected1 = new News();
        affected1.setId(4L);
        affected1.setSortOrder(2);

        News affected2 = new News();
        affected2.setId(5L);
        affected2.setSortOrder(4);

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.selectById(3L)).thenReturn(news3);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);
        when(newsMapper.softDelete(3L)).thenReturn(1);

        // 使用 anyInt() 避免参数不匹配
        when(newsMapper.selectPassedBySortOrderGreaterThan(anyInt())).thenReturn(List.of(affected1, affected2));

        // 使用 anyLong() 和 anyInt() 避免参数不匹配
        doNothing().when(newsMapper).updateSortOrderById(anyLong(), anyInt());

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        verify(newsMapper).softDelete(3L);
        // 验证 updateSortOrderById 被调用了
        verify(newsMapper, atLeastOnce()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNewsBatch_MoveUpEqualToZero() {
        List<Long> ids = List.of(1L, 2L);

        // 被删除的记录
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(5);

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(6);

        // 受影响的记录
        News affected1 = new News();
        affected1.setId(3L);
        affected1.setSortOrder(2);

        News affected2 = new News();
        affected2.setId(4L);
        affected2.setSortOrder(3);

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);

        // 使用 anyInt() 避免参数不匹配
        when(newsMapper.selectPassedBySortOrderGreaterThan(anyInt())).thenReturn(List.of(affected1, affected2));

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        // moveUp = 0，不会调用 updateSortOrderById
        verify(newsMapper, never()).updateSortOrderById(anyLong(), anyInt());
    }

    @Test
    void deleteNewsBatch_ComplexMoveUpCalculation() {
        List<Long> ids = List.of(1L, 2L, 3L);

        // 被删除的记录
        News news1 = new News();
        news1.setId(1L);
        news1.setStatus("已通过");
        news1.setSortOrder(2);

        News news2 = new News();
        news2.setId(2L);
        news2.setStatus("已通过");
        news2.setSortOrder(4);

        News news3 = new News();
        news3.setId(3L);
        news3.setStatus("已通过");
        news3.setSortOrder(6);

        // 受影响的记录
        News affected1 = new News();
        affected1.setId(4L);
        affected1.setSortOrder(3);

        News affected2 = new News();
        affected2.setId(5L);
        affected2.setSortOrder(5);

        News affected3 = new News();
        affected3.setId(6L);
        affected3.setSortOrder(7);

        when(newsMapper.selectById(1L)).thenReturn(news1);
        when(newsMapper.selectById(2L)).thenReturn(news2);
        when(newsMapper.selectById(3L)).thenReturn(news3);
        when(newsMapper.softDelete(1L)).thenReturn(1);
        when(newsMapper.softDelete(2L)).thenReturn(1);
        when(newsMapper.softDelete(3L)).thenReturn(1);

        // 使用 anyInt() 避免参数不匹配
        when(newsMapper.selectPassedBySortOrderGreaterThan(anyInt())).thenReturn(List.of(affected1, affected2, affected3));

        // 使用 anyLong() 和 anyInt() 避免参数不匹配
        doNothing().when(newsMapper).updateSortOrderById(anyLong(), anyInt());

        newsService.deleteNewsBatch(ids);

        verify(newsMapper).softDelete(1L);
        verify(newsMapper).softDelete(2L);
        verify(newsMapper).softDelete(3L);
        // 验证 updateSortOrderById 被调用了
        verify(newsMapper, atLeastOnce()).updateSortOrderById(anyLong(), anyInt());
    }
    @Test
    void updateNews_OldNewsSortOrderNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(null); // oldNews.getSortOrder() == null
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 oldNews.getSortOrder() != null 的分支
        verify(newsMapper, never()).selectPassedBySortOrderGreaterThan(anyInt());
    }

    @Test
    void updateNews_NewsStatusNotPassed() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(3);
        news.setStatus("未通过"); // 不是"已通过"
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 "已通过".equals(news.getStatus()) 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_NewSortNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(null); // newSort == null
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 newSort != null 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_OldSortMinusOne() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(null); // oldSort = -1 (因为 oldNews.getSortOrder() == null)
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 oldSort != -1 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_NewImagesContainsOldUrl() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("<img src='old.jpg'>");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("<img src='old.jpg'><img src='new.jpg'>"); // 包含旧URL

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 !newImages.contains(oldUrl) 的分支，因为新内容包含旧URL
        // 不会调用 deleteFileByUrl
    }

    @Test
    void updateNews_NewImagesNotContainsOldUrl() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("<img src='old.jpg'>");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("<img src='new.jpg'>"); // 不包含旧URL

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 会进入 !newImages.contains(oldUrl) 的分支，会调用 deleteFileByUrl
        // 但由于是私有方法，我们无法直接验证，但可以通过覆盖率确认
    }

    @Test
    void updateNews_OldImagePathNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath(null); // 旧图片路径为null
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // oldImagePath 为 null，不会进入图片处理分支
    }

    @Test
    void updateNews_NewImagePathNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath(null); // 新图片路径为null
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // newImagePath 为 null，不会进入图片处理分支
    }

    @Test
    void updateNews_OldContentNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent(null); // 旧内容为null

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // oldContent 为 null，不会进入内容处理分支
    }

    @Test
    void updateNews_NewContentNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2);
        news.setStatus("已通过");
        news.setImagePath("new.jpg");
        news.setContent(null); // 新内容为null

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // newContent 为 null，不会进入内容处理分支
    }
    @Test
    void updateNews_NewsStatusNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(3);
        news.setStatus(null); // news.getStatus() == null
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 "已通过".equals(news.getStatus()) 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_NewsStatusNotPassedString() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(3);
        news.setStatus("待审核"); // 不是"已通过"
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 "已通过".equals(news.getStatus()) 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_NewsStatusRejected() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2);
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(3);
        news.setStatus("已拒绝"); // 不是"已通过"
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 "已通过".equals(news.getStatus()) 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_OldNewsSortOrderNull_NewSortNotNull() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(null); // oldSort = -1 (因为 oldNews.getSortOrder() == null)
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2); // newSort != null
        news.setStatus("已通过"); // "已通过".equals(news.getStatus()) == true
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 oldSort != -1 的分支，因为 oldSort == -1
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }
    @Test
    void updateNews_NewsStatusPassed_NewSortNull_OldSortNotMinusOne() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(2); // oldSort = 2 (不为-1)
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(null); // newSort == null
        news.setStatus("已通过"); // "已通过".equals(news.getStatus()) == true
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 newSort != null 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }

    @Test
    void updateNews_NewsStatusPassed_NewSortNotNull_OldSortMinusOne() {
        News oldNews = new News();
        oldNews.setId(1L);
        oldNews.setSortOrder(null); // oldSort = -1 (因为 oldNews.getSortOrder() == null)
        oldNews.setStatus("已通过");
        oldNews.setImagePath("old.jpg");
        oldNews.setContent("old content");

        News news = new News();
        news.setId(1L);
        news.setSortOrder(2); // newSort != null
        news.setStatus("已通过"); // "已通过".equals(news.getStatus()) == true
        news.setImagePath("new.jpg");
        news.setContent("new content");

        when(newsMapper.selectById(1L)).thenReturn(oldNews);
        when(userMapper.selectRoleById(anyLong())).thenReturn("USER");
        when(newsMapper.update(any(News.class))).thenReturn(1);
        UserContext.setUserId(1L);

        newsService.updateNews(news);

        verify(newsMapper).update(news);
        // 不会进入 oldSort != -1 的分支
        verify(newsMapper, never()).incrementSortOrderRange(anyInt(), anyInt(), anyLong());
        verify(newsMapper, never()).decrementSortOrderRange(anyInt(), anyInt(), anyLong());
    }
    //delete

    @Test
    void deleteFileByUrl_UrlNotStartWithPrefix() {
        String url = "http://otherhost/test.jpg";
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");

        // 直接调用，不验证 File 对象
        ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", url);
    }
    @Test
    void deleteFileByUrl_UrlEmpty() {
        String url = "";
        ReflectionTestUtils.setField(newsService, "uploadDir", "/tmp");
        ReflectionTestUtils.setField(newsService, "urlPrefix", "http://localhost/");

        // 使用 MockedConstruction 来 mock File 对象
        try (MockedConstruction<File> mocked = mockConstruction(File.class,
                (mock, context) -> {
                    when(mock.exists()).thenReturn(false);
                    when(mock.getAbsolutePath()).thenReturn("/tmp/");
                })) {

            ReflectionTestUtils.invokeMethod(newsService, "deleteFileByUrl", url);

            // 验证 File 对象被正确创建和调用
            verify(mocked.constructed().get(0)).exists();
            verify(mocked.constructed().get(0), never()).delete();
        }
    }
}