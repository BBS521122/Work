package com.work.work.service.Impl;

import com.work.work.entity.News;
import com.work.work.mapper.sql.NewsMapper;
import com.work.work.service.NewsService;
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

        if ("已通过".equals(news.getStatus())) {
            // 只有通过的新闻才分配排序值
            if (news.getSortOrder() == null) {
                // 若前端没指定，默认插入到最后
                Integer maxSort = newsMapper.selectMaxSortOrder();
                news.setSortOrder(maxSort != null ? maxSort + 1 : 1);
            }

            // 调整其他记录
            List<News> affected = newsMapper.selectPassedBySortOrderGreaterThanEqual(news.getSortOrder());
            for (News n : affected) {
                int newOrder = n.getSortOrder() + 1;
                n.setSortOrder(newOrder);
                newsMapper.updateSortOrderById(n.getId(), newOrder);
            }
        } else {
            // 未通过的资讯不参与排序
            news.setSortOrder(null);
        }

        newsMapper.insert(news);
    }



    @Override
    @Transactional
    public void updateNews(News news) {
        if (news.getId() == null) throw new IllegalArgumentException("ID不能为空");

        News oldNews = newsMapper.selectById(news.getId());
        if (oldNews == null) throw new RuntimeException("记录不存在");

        // 获取当前登录用户是否为管理员（假设 future 你使用了 Spring Security）
        boolean isAdmin = true;


        if (!isAdmin) {
            // 普通用户修改“已通过”或“已拒绝”的新闻
            if ("已通过".equals(oldNews.getStatus()) || "已拒绝".equals(oldNews.getStatus())) {
                news.setStatus("待审核");
                if (oldNews.getSortOrder() != null) {
                    // 需要清理排序空位
                    int deletedOrder = oldNews.getSortOrder();

                    List<News> affected = newsMapper.selectPassedBySortOrderGreaterThan(deletedOrder);
                    for (News n : affected) {
                        int newOrder = n.getSortOrder() - 1;
                        newsMapper.updateSortOrderById(n.getId(), newOrder);
                    }

                }
            }
        } else {
            // 管理员才允许调整排序
            int oldSort = oldNews.getSortOrder() == null ? -1 : oldNews.getSortOrder();
            Integer newSort = news.getSortOrder();

            if ("已通过".equals(news.getStatus()) && newSort != null && oldSort != -1) {
                if (!newSort.equals(oldSort)) {
                    if (newSort < oldSort) {
                        // 向前移动：将 [newSort, oldSort-1] 的新闻 +1
                        newsMapper.incrementSortOrderRange(newSort, oldSort - 1, news.getId());
                    } else {
                        // 向后移动：将 [oldSort+1, newSort] 的新闻 -1
                        newsMapper.decrementSortOrderRange(oldSort + 1, newSort, news.getId());
                    }
                }
            }
        }

        // ✅ 清理内容差异部分的图片
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

        // 最终更新记录（含排序变更/状态变更）
        newsMapper.update(news);
    }





    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("记录不存在或已被删除");
        }

        // 逻辑删除
        newsMapper.softDelete(id);

        // 仅“已通过”并且有排序值的新闻，才需要调整排序
        if ("已通过".equals(news.getStatus()) && news.getSortOrder() != null) {
            int deletedOrder = news.getSortOrder();

            // 查询所有比它大的“已通过”新闻
            List<News> affected = newsMapper.selectPassedBySortOrderGreaterThan(deletedOrder);

            // 批量调整这些记录的排序值
            for (News n : affected) {
                int newOrder = n.getSortOrder() - 1;
                newsMapper.updateSortOrderById(n.getId(), newOrder);
            }
        }
    }
    @Override
    @Transactional
    public void deleteNewsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        // 拿到所有要删除的记录
        List<News> toDelete = ids.stream()
                .map(newsMapper::selectById)
                .filter(Objects::nonNull)
                .toList();

        // 拆分“已通过的”和“其他的”
        List<News> passedToDelete = toDelete.stream()
                .filter(n -> "已通过".equals(n.getStatus()) && n.getSortOrder() != null)
                .sorted(Comparator.comparingInt(News::getSortOrder))
                .toList();

        Set<Long> idSet = toDelete.stream().map(News::getId).collect(Collectors.toSet());

        // 1. 所有记录都逻辑删除
        for (Long id : idSet) {
            newsMapper.softDelete(id);
        }

        // 2. 仅“已通过”的需要处理排序
        if (!passedToDelete.isEmpty()) {
            Set<Integer> deletedOrders = passedToDelete.stream()
                    .map(News::getSortOrder)
                    .collect(Collectors.toSet());

            int minDeletedOrder = passedToDelete.get(0).getSortOrder();

            // 查询所有比最小排序值大的“已通过”新闻
            List<News> affected = newsMapper.selectPassedBySortOrderGreaterThan(minDeletedOrder);

            for (News n : affected) {
                // 看看这个排序值前面有几个被删了，向前挪动
                int moveUp = (int) deletedOrders.stream().filter(o -> o < n.getSortOrder()).count();
                if (moveUp > 0) {
                    int newOrder = n.getSortOrder() - moveUp;
                    newsMapper.updateSortOrderById(n.getId(), newOrder);
                }
            }
        }
    }



    @Override
    public List<News> getNewsByStatus(String status) {
        return newsMapper.selectByStatus(status);
    }


    @Override
    public News getNewsById(Long id) {
        return newsMapper.selectById(id);
    }

    @Override
    public List<News> getAllDeletedNews() {
        return newsMapper.selectDeleted();
    }

    @Override
    public List<News> getDeletedNewsByTenant(Long tenantId) {
        return newsMapper.selectDeletedByTenant(tenantId);
    }


    @Override
    @Transactional
    public void restoreNews(Long id) {
        News news = newsMapper.selectByIdIncludingDeleted(id);
        if (news == null) throw new RuntimeException("记录不存在");

        boolean isAdmin = true; // 临时代替权限系统

        newsMapper.restore(id); // 先还原 is_deleted = 0，后续 update 才能成功

        if (isAdmin) {
            String status = news.getStatus();

            if ("已通过".equals(status) && news.getSortOrder() != null) {
                Integer targetOrder = news.getSortOrder();
                Integer maxSort = newsMapper.selectMaxSortOrder();
                int currentMax = maxSort != null ? maxSort : -1;

                if (targetOrder > currentMax) {
                    targetOrder = ++currentMax;
                    news.setSortOrder(targetOrder);
                }

                List<News> affected = newsMapper.selectPassedBySortOrderGreaterThanEqual(targetOrder);
                for (News n : affected) {
                    newsMapper.updateSortOrderById(n.getId(), n.getSortOrder() + 1);
                }

            } else {
                news.setSortOrder(null);
            }

            newsMapper.update(news);

        } else {
            news.setStatus("待审核");
            news.setSortOrder(null);
            newsMapper.update(news);
        }
    }
    @Override
    @Transactional
    public void restoreNewsBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        boolean isAdmin = true;

        List<News> toRestore = ids.stream()
                .map(newsMapper::selectByIdIncludingDeleted)
                .filter(Objects::nonNull)
                .toList();

        newsMapper.restoreBatch(ids); // ✅ 必须先还原 is_deleted=0

        if (isAdmin) {
            List<News> existing = newsMapper.selectAll().stream()
                    .filter(n -> "已通过".equals(n.getStatus()) && n.getSortOrder() != null)
                    .sorted(Comparator.comparingInt(News::getSortOrder).reversed())
                    .collect(Collectors.toList());

            Integer maxSort = newsMapper.selectMaxSortOrder();
            int currentMax = maxSort != null ? maxSort : -1;

            for (News news : toRestore) {
                String status = news.getStatus();

                if ("已通过".equals(status) && news.getSortOrder() != null) {
                    Integer sort = news.getSortOrder();

                    if (sort > currentMax) {
                        sort = ++currentMax;
                        news.setSortOrder(sort);
                    }

                    List<News> affected = newsMapper.selectPassedBySortOrderGreaterThanEqual(sort);
                    for (News n : affected) {
                        newsMapper.updateSortOrderById(n.getId(), n.getSortOrder() + 1);
                    }

                } else {
                    news.setSortOrder(null);
                }

                newsMapper.update(news);
            }

        } else {
            for (News news : toRestore) {
                news.setStatus("待审核");
                news.setSortOrder(null);
                newsMapper.update(news);
            }
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
    @Override
    @Transactional
    public void approveNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || !"待审核".equals(news.getStatus())) {
            throw new RuntimeException("记录不存在或状态错误");
        }

        // 确定目标排序值
        Integer targetSort = news.getSortOrder();

        if (targetSort == null) {
            // 如果没有指定排序值，默认加在最后
            Integer maxSort = newsMapper.selectMaxSortOrder();
            targetSort = (maxSort != null ? maxSort + 1 : 1);
        } else {
            // 如果用户请求了特定排序值，需要调整已通过记录的排序空位
            List<News> affected = newsMapper.selectPassedBySortOrderGreaterThanEqual(targetSort);
            for (News n : affected) {
                newsMapper.updateSortOrderById(n.getId(), n.getSortOrder() + 1);
            }
        }

        news.setSortOrder(targetSort);
        news.setStatus("已通过");

        newsMapper.updateSortOrderById(news.getId(), targetSort);
        newsMapper.updateStatus(news.getId(), "已通过");
    }


    @Override
    public void rejectNews(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || news.getStatus() == null || !"待审核".equals(news.getStatus())) {
            throw new RuntimeException("记录不存在或状态不正确");
        }
        news.setStatus("已拒绝");
        newsMapper.updateStatus(id, "已拒绝");
    }
    @Override
    public List<News> getNewsByTenantId(Long tenantId) {
        return newsMapper.selectByTenantId(tenantId);
    }

}
