package com.example.demo.ServiceImpl;

import com.example.demo.Entity.News;
import com.example.demo.Mapper.NewsMapper;
import com.example.demo.Service.NewsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Value("${news.upload.dir}")
    private String uploadDir;

    @Value("${news.url.prefix}")
    private String urlPrefix;

    @Override
    @Transactional
    public void addNews(News news) {
        validateNews(news);

        // 插入排序处理：将所有 >= 新插入排序值的记录往后挪
        if (news.getSortOrder() != null) {
            List<News> affected = newsMapper.selectBySortOrderGreaterThanEqual(news.getSortOrder());
            for (News n : affected) {
                int newOrder = n.getSortOrder() + 1;
                n.setSortOrder(newOrder);
                newsMapper.updateSortOrderById(n.getId(), newOrder);
            }
        }

        newsMapper.insert(news);
    }


    @Override
    @Transactional
    public void updateNews(News news) {
        if (news.getId() == null) throw new IllegalArgumentException("ID不能为空");

        News oldNews = newsMapper.selectById(news.getId());
        if (oldNews == null) throw new RuntimeException("记录不存在");

        int oldSort = oldNews.getSortOrder();
        int newSort = news.getSortOrder();

        if (newSort != oldSort) {
            if (newSort < oldSort) {
                // 向前移动：将 [newSort, oldSort-1] 的新闻 +1
                newsMapper.incrementSortOrderRange(newSort, oldSort - 1, news.getId());
            } else {
                // 向后移动：将 [oldSort+1, newSort] 的新闻 -1
                newsMapper.decrementSortOrderRange(oldSort + 1, newSort, news.getId());
            }
        }

        // ✅ 清理内容差异部分的图片（保留你已有逻辑）
        if (!Objects.equals(news.getImagePath(), oldNews.getImagePath())) {
            deleteFileByUrl(oldNews.getImagePath());
        }
        List<String> oldImages = extractMediaUrls(oldNews.getContent());
        List<String> newImages = extractMediaUrls(news.getContent());
        for (String oldUrl : oldImages) {
            if (!newImages.contains(oldUrl)) {
                deleteFileByUrl(oldUrl);
            }
        }

        newsMapper.update(news); // ✅ 更新最终排序值
    }




    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("记录不存在或已被删除");
        }

        int deletedOrder = news.getSortOrder();
        newsMapper.softDelete(id);

        // 后移排序前移
        List<News> affected = newsMapper.selectBySortOrderGreaterThan(deletedOrder);
        for (News n : affected) {
            n.setSortOrder(n.getSortOrder() - 1);
            newsMapper.updateSortOrderById(n.getId(), n.getSortOrder());
        }
    }


    @Override
    public List<News> getAllNews() {
        return newsMapper.selectAll();
    }

    @Override
    public News getNewsById(Long id) {
        return newsMapper.selectById(id);
    }

    @Override
    public List<News> getDeletedNews() {
        return newsMapper.selectDeleted();
    }

    @Override
    public void restoreNews(Long id) {
        News news = newsMapper.selectByIdIncludingDeleted(id);
        if (news == null) throw new RuntimeException("记录不存在");

        List<News> existing = newsMapper.selectAll();
        int targetOrder = news.getSortOrder() != null ? news.getSortOrder() : 0;
        existing.sort((a, b) -> b.getSortOrder() - a.getSortOrder());

        for (News n : existing) {
            if (n.getSortOrder() >= targetOrder) {
                n.setSortOrder(n.getSortOrder() + 1);
                newsMapper.update(n);
            }
        }

        newsMapper.restore(id);
        news.setSortOrder(targetOrder);
        newsMapper.update(news);
    }

    @Override
    public void restoreNewsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<News> toRestore = new ArrayList<>();
        for (Long id : ids) {
            News n = newsMapper.selectByIdIncludingDeleted(id);
            if (n != null) toRestore.add(n);
        }
        toRestore.sort(Comparator.comparingInt(n -> n.getSortOrder() != null ? n.getSortOrder() : 0));

        List<News> existing = newsMapper.selectAll();
        for (News restoreNews : toRestore) {
            int targetOrder = restoreNews.getSortOrder() != null ? restoreNews.getSortOrder() : 0;
            existing.sort((a, b) -> b.getSortOrder() - a.getSortOrder());
            for (News n : existing) {
                if (n.getSortOrder() >= targetOrder) {
                    n.setSortOrder(n.getSortOrder() + 1);
                    newsMapper.update(n);
                }
            }
            newsMapper.restore(restoreNews.getId());
            newsMapper.update(restoreNews);
            existing.add(restoreNews);
        }
    }

    @Override
    public void hardDeleteNews(Long id) {
        News news = newsMapper.selectByIdIncludingDeleted(id);
        if (news == null) throw new RuntimeException("记录不存在");

        if (news.getImagePath() != null) {
            deleteFileByUrl(news.getImagePath());
        }
        for (String url : extractMediaUrls(news.getContent())) {
            deleteFileByUrl(url);
        }

        int rows = newsMapper.hardDelete(id);
        if (rows == 0) throw new RuntimeException("物理删除失败，记录不存在或未被删除");
    }

    @Override
    public void hardDeleteNewsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        for (Long id : ids) {
            News news = newsMapper.selectByIdIncludingDeleted(id);
            if (news != null) {
                if (news.getImagePath() != null) {
                    deleteFileByUrl(news.getImagePath());
                }
                for (String url : extractMediaUrls(news.getContent())) {
                    deleteFileByUrl(url);
                }
            }
        }

        int affected = newsMapper.hardDeleteBatch(ids);
        if (affected != ids.size()) {
            throw new RuntimeException("部分新闻彻底删除失败");
        }
    }
    @Override
    @Transactional
    public void deleteNewsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        // 取出所有要删除的记录，并按 sortOrder 升序排序
        List<News> toDelete = ids.stream()
                .map(newsMapper::selectById)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(News::getSortOrder))
                .toList();

        Set<Long> idSet = toDelete.stream().map(News::getId).collect(Collectors.toSet());
        Set<Integer> deletedOrders = toDelete.stream().map(News::getSortOrder).collect(Collectors.toSet());

        // 逻辑删除这些记录
        for (Long id : idSet) {
            newsMapper.softDelete(id);
        }

        // 所有未删除记录中，排序值比最小删除值大的
        int minDeletedOrder = toDelete.get(0).getSortOrder();
        List<News> affected = newsMapper.selectBySortOrderGreaterThan(minDeletedOrder);

        for (News n : affected) {
            int moveUp = (int) deletedOrders.stream().filter(o -> o < n.getSortOrder()).count();
            if (moveUp > 0) {
                int newOrder = n.getSortOrder() - moveUp;
                n.setSortOrder(newOrder);
                newsMapper.updateSortOrderById(n.getId(), newOrder);
            }
        }
    }


    private void validateNews(News news) {
        if (news.getTitle() == null || news.getTitle().isBlank()
                || news.getSummary() == null || news.getSummary().isBlank()
                || news.getContent() == null || news.getContent().isBlank()
                || news.getAuthor() == null || news.getAuthor().isBlank()
                || news.getImagePath() == null || news.getImagePath().isBlank()) {
            throw new IllegalArgumentException("所有字段不能为空");
        }
    }

    private List<String> extractMediaUrls(String html) {
        List<String> urls = new ArrayList<>();
        if (html == null || html.isBlank()) return urls;

        Pattern pattern = Pattern.compile(
                "<(?:img|video|source)[^>]+src=[\"']([^\"']+)[\"'][^>]*>",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }

    private void deleteFileByUrl(String url) {
        if (url != null && url.startsWith(urlPrefix)) {
            String filename = url.substring(urlPrefix.length());
            File file = new File(uploadDir, filename);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("删除文件失败: " + file.getAbsolutePath());
                }
            }
        }
    }
}
