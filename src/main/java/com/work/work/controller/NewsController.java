package com.work.work.controller;

import com.work.work.entity.News;
import com.work.work.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    @Value("${news.upload.dir}")
    private String uploadDir;

    @Value("${news.url.prefix}")
    private String urlPrefix;

    @Autowired
    private NewsService newsService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return ResponseEntity.badRequest().body(Map.of("message", "文件名无效"));
            }

            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            String[] allowed = {".jpg", ".jpeg", ".png", ".gif", ".mp4", ".webm"};
            boolean allowedType = Arrays.stream(allowed).anyMatch(suffix::equals);
            if (!allowedType) {
                return ResponseEntity.badRequest().body(Map.of("message", "不支持的文件类型"));
            }

            String filename = UUID.randomUUID().toString() + suffix;
            File destFile = new File(uploadDir, filename);

            // ✅ 写入文件
            file.transferTo(destFile);

            // ✅ 确保文件写入成功
            int retry = 0;
            while (!destFile.exists() && retry < 5) {
                Thread.sleep(100); // 最多等待 500ms
                retry++;
            }

            // ✅ 可选：进一步延迟等待文件系统同步
            Thread.sleep(100);

            return ResponseEntity.ok(Map.of("url", urlPrefix + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "上传失败"));
        }
    }



    @DeleteMapping("/deleteMedia")
    public ResponseEntity<?> deleteMedia(@RequestParam String url) {
        try {
            if (url.startsWith(urlPrefix)) {
                String filename = url.substring(urlPrefix.length());
                File file = new File(uploadDir, filename);
                if (file.exists()) file.delete();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "图片/视频删除失败"));
        }
    }

    @GetMapping
    public List<News> list() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        News news = newsService.getNewsById(id);
        if (news == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(news);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody News news) {
        try {
            newsService.addNews(news);
            return ResponseEntity.ok(Map.of("message", "创建成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "服务器内部错误"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody News news) {
        try {
            news.setId(id);
            newsService.updateNews(news);
            return ResponseEntity.ok(Map.of("message", "修改成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "服务器内部错误"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "服务器内部错误"));
        }
    }
    // 获取回收站新闻列表
    @GetMapping("/recycle-bin")
    public List<News> getRecycleBin() {
        return newsService.getDeletedNews();
    }
    // 恢复回收站新闻
    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable Long id) {
        try {
            newsService.restoreNews(id);
            return ResponseEntity.ok(Map.of("message", "恢复成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    // 彻底删除回收站新闻（可选）
    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<?> hardDelete(@PathVariable Long id) {
        try {
            newsService.hardDeleteNews(id);
            return ResponseEntity.ok(Map.of("message", "彻底删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    /**
     * 批量恢复回收站新闻
     * 接收新闻id列表，恢复对应新闻
     */
    @PutMapping("/restore-batch")
    public ResponseEntity<?> restoreBatch(@RequestBody List<Long> ids) {
        try {
            newsService.restoreNewsBatch(ids);
            return ResponseEntity.ok(Map.of("message", "批量恢复成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "批量恢复失败：" + e.getMessage()));
        }
    }

    /**
     * 批量彻底删除回收站新闻
     */
    @DeleteMapping("/hard-delete-batch")
    public ResponseEntity<?> hardDeleteBatch(@RequestBody List<Long> ids) {
        try {
            newsService.hardDeleteNewsBatch(ids);
            return ResponseEntity.ok(Map.of("message", "批量彻底删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "批量彻底删除失败：" + e.getMessage()));
        }
    }
    @DeleteMapping("/delete-batch")
    public ResponseEntity<?> deleteBatch(@RequestBody List<Long> ids) {
        try {
            newsService.deleteNewsBatch(ids);
            return ResponseEntity.ok(Map.of("message", "批量删除成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "批量删除失败：" + e.getMessage()));
        }
    }


}
