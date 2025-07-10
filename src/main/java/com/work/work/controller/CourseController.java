package com.work.work.controller;

import com.work.work.config.MinioConfig;
import com.work.work.dto.AddCourseDTO;
import com.work.work.dto.ChapterAddDTO;
import com.work.work.dto.ChapterAddItemDTO;
import com.work.work.entity.Course;
import com.work.work.entity.Chapter;
import com.work.work.mapper.ChapterMapper;
import com.work.work.mapper.CourseMapper;
import com.work.work.properties.MinioProperties;
import com.work.work.service.CourseService;
import com.work.work.service.MinioService;
import com.work.work.vo.CourseIndexMobileVO;
import com.work.work.vo.CourseInfoMobileVO;
import com.work.work.vo.CourseOverviewMobileVO;
import com.work.work.vo.HttpResponseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

@RestController()
@RequestMapping("/course")
@CrossOrigin
public class CourseController {
    @Autowired
    MinioService minioService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CourseService courseService;
    @Autowired
    private MinioProperties minioProperties;

    /**
     * 上传课程
     *
     * @param addCourseDTO
     * @return
     */
    @PostMapping("/add-course")
    public HttpResponseEntity<Long> addCourse(@RequestBody AddCourseDTO addCourseDTO) {

        try {
            // 将DTO转换为实体
            Course course = new Course();
            BeanUtils.copyProperties(addCourseDTO, course);

            // 设置创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            course.setCourseCreateTime(now);
            course.setCourseUpdateTime(now);

            course.setState(0);

            // 保存到数据库
            int result = courseMapper.addCourse(course);

            if (result > 0) {
                return new HttpResponseEntity<>(200, course.getId(), "课程保存成功");
            } else {
                return new HttpResponseEntity<>(500, null, "保存课程失败");
            }
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, null, "服务器错误: " + e.getMessage());
        }
    }

    /**
     * 更新课程章节
     * 思路：
     * 删除chapter表中courseId对应的所有信息
     * 之后插入前端传来的信息
     *
     * @param chapterAddDTO
     * @return
     */
    @PostMapping("/add-update-course")
    public HttpResponseEntity<Integer> addUpdateCourse(@RequestBody ChapterAddDTO chapterAddDTO) {
        try {
            Long courseId = chapterAddDTO.getCourseId();
            List<ChapterAddItemDTO> chapters = chapterAddDTO.getChapters();

            LocalDateTime now=LocalDateTime.now();
            courseMapper.updateTimeById(courseId,now);

            // 1. 删除该课程的所有现有章节
            chapterMapper.deleteChaptersByCourseId(courseId);

            // 2. 插入新的章节
            for (ChapterAddItemDTO item : chapters) {
                String name=item.getName();

                Chapter chapter = new Chapter();
                chapter.setCourseId(courseId);
                chapter.setName(item.getName());
                chapter.setOrder(item.getOrder());
                String videoUrl = item.getVideoUrl();
                String bucket = "/"+minioProperties.getBucket()+"/";
                if (videoUrl.startsWith("http")) {
                    int dataIndex = videoUrl.indexOf(bucket);
                    if (dataIndex != -1) {
                        int questionMarkIndex = videoUrl.indexOf("?", dataIndex);
                        if (questionMarkIndex != -1) {
                            String extractedPart = videoUrl.substring(dataIndex + bucket.length(), questionMarkIndex);
                            chapter.setVideoUrl(extractedPart);
                        } else {
                            // 没有找到问号，直接截取到最后
                            String extractedPart = videoUrl.substring(dataIndex + bucket.length());
                            chapter.setVideoUrl(extractedPart);
                        }
                    } else {
                        // 不包含 /data2/，按原样设置
                        chapter.setVideoUrl(videoUrl);
                    }
                }else{
                    chapter.setVideoUrl(videoUrl);
                }
                chapterMapper.insertChapter(chapter);
            }
            return new HttpResponseEntity<>(200, chapters.size(), "课程章节更新成功");
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, 0, "服务器错误: " + e.getMessage());
        }
    }

    /**
     * 上传音视频文件
     * 返回文件名字
     */
    @PostMapping("/upload")
    public HttpResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String uuid = courseService.updateCourse(file);
        return new HttpResponseEntity<>(200, uuid, "success");
    }


    /**
     * 获取所有课程信息
     */
    @GetMapping("/get-all-course")
    public HttpResponseEntity<List<Course>> allCourse() {
        List<Course> courses = courseMapper.getallCourse();
        courses.forEach(course -> {
            course.setCoverUrl(minioService.getSignedUrl(course.getCoverUrl()));
        });
        return new HttpResponseEntity<>(200, courses, "success");
    }

    /**
     * 根据课程id获取课程
     *
     * @param courseId
     * @return
     */
    @GetMapping("/get-course-info")
    public HttpResponseEntity<Course> getCourseInfo(@RequestParam("courseId") Long courseId) {
        try {
            // 根据ID获取课程
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return new HttpResponseEntity<>(404, null, "课程不存在");
            }
            // 生成封面的临时访问URL
            course.setCoverUrl(minioService.getSignedUrl(course.getCoverUrl()));
            return new HttpResponseEntity<>(200, course, "success");
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, null, "服务器错误: " + e.getMessage());
        }
    }

    /**
     * 删除课程
     * 注意：要删除chapter表和minio相关信息
     *
     * @return
     */
    @GetMapping("/delete-course")
    public HttpResponseEntity<Integer> deleteCourse(@RequestParam("courseId") Long courseId) {
        try {
            // 检查课程ID是否有效
            if (courseId == null || courseId <= 0) {
                return new HttpResponseEntity<>(400, 0, "无效的课程ID");
            }

            // 获取要删除的课程信息
            Course course = courseMapper.getCourseById(courseId);
            if (course == null) {
                return new HttpResponseEntity<>(404, 0, "课程不存在");
            }

            // 删除Minio中的文件
            if (course.getCoverUrl() != null && !course.getCoverUrl().isEmpty()) {
                minioService.deleteFile(course.getCoverUrl());
            }
            // 创建只包含单个courseId的列表
            List<Long> courseIdList = Collections.singletonList(courseId);

            // 调用批量删除方法（虽然只删除单个课程）
            int deletedCount = courseMapper.deleteCourses(courseIdList);

            if (deletedCount > 0) {
                return new HttpResponseEntity<>(200, deletedCount, "课程删除成功");
            } else {
                return new HttpResponseEntity<>(500, 0, "课程删除失败");
            }
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, 0, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 修改课程
     *
     * @param course
     * @return
     */
    @PostMapping("/update-course")
    public HttpResponseEntity<Integer> updateCourse(@RequestBody Course course) {
        try {
            // 1. 获取原课程信息
            Course originalCourse = courseMapper.getCourseById(course.getId());
            if (originalCourse == null) {
                return new HttpResponseEntity<>(404, 0, "未找到课程信息");
            }

            // 2. 处理封面更新
            if (course.getCoverUrl() != null) {
                // 如果提供了新封面URL，删除旧封面
                if (originalCourse.getCoverUrl() != null &&
                        !originalCourse.getCoverUrl().isEmpty()) {
                    minioService.deleteFile(originalCourse.getCoverUrl());
                }
            } else {
                // 保留原封面
                course.setCoverUrl(originalCourse.getCoverUrl());
            }

            // 4. 设置更新时间
            course.setCourseUpdateTime(LocalDateTime.now());

            // 5. 更新数据库
            int result = courseMapper.updateCourse(course);

            if (result > 0) {
                return new HttpResponseEntity<>(200, 1, "课程更新成功");
            } else {
                return new HttpResponseEntity<>(500, 0, "更新课程失败");
            }
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, 0, "服务器错误: " + e.getMessage());
        }
    }

    /**
     * 获取课程的章节
     *
     * @param courseId
     * @return
     */
    @GetMapping("/get-chapter")
    public HttpResponseEntity<List<Chapter>> getChapter(@RequestParam("courseId") Long courseId) {
        try {
            // 1. 获取基础章节数据
            List<Chapter> chapters = chapterMapper.getChaptersByCourseId(courseId);

            // 2. 为每个章节生成视频临时访问URL
            for (Chapter chapter : chapters) {
                if (chapter.getVideoUrl() != null && !chapter.getVideoUrl().isEmpty()) {
                    String signedUrl = minioService.getSignedUrl(chapter.getVideoUrl());
                    chapter.setVideoUrl(signedUrl);
                }
            }

            return new HttpResponseEntity<>(200, chapters, "章节获取成功");
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, null, "服务器错误: " + e.getMessage());
        }
    }


    /**
     * 移动端获取课程
     *
     * @param query
     * @return
     */
    @GetMapping("/mobile/get-courses")
    public HttpResponseEntity<List<CourseOverviewMobileVO>> getMobileCourses(@RequestParam String query) {
        try {
            // 1. 获取课程列表
            List<Course> courses = courseMapper.getMobileCourses();

            // 2. 转换为VO并生成封面URL
            List<CourseOverviewMobileVO> result = new ArrayList<>();
            for (Course course : courses) {
                // 根据查询条件过滤
                if (query != null && !query.isEmpty()) {
                    String q = query.toLowerCase();
                    if (!course.getCourseName().toLowerCase().contains(q) &&
                            !course.getCourseDescription().toLowerCase().contains(q)) {
                        continue;
                    }
                }

                // 生成封面URL
                String coverUrl = course.getCoverUrl();
                if (coverUrl != null && !coverUrl.isEmpty()) {
                    coverUrl = minioService.getSignedUrl(coverUrl);
                }

                // 创建VO对象 - 使用 creator 字段
                CourseOverviewMobileVO vo = new CourseOverviewMobileVO(
                        course.getId(),
                        coverUrl,
                        course.getCourseName(),
                        course.getCourseAuthor() // 对应 creator 字段
                );
                result.add(vo);
            }

            return new HttpResponseEntity<>(200, result, "success");
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, null, "服务器错误: " + e.getMessage());
        }
    }

    /**
     * 根据id获取移动端
     *
     * @param id
     * @return
     */
    @GetMapping("/mobile/get-info")
    public HttpResponseEntity<CourseInfoMobileVO> getCourseInfoMobile(@RequestParam Long id) {
        try {
            // 1. 获取课程基本信息
            Course course = courseMapper.getMobileCourseInfo(id);
            if (course == null) {
                return new HttpResponseEntity<>(404, null, "课程不存在或未通过审核");
            }

            // 2. 获取课程章节
            List<Chapter> chapters = chapterMapper.getChaptersByCourseId(id);
            List<CourseIndexMobileVO> chapterList = new ArrayList<>();

            for (Chapter chapter : chapters) {
                // 生成视频URL
                String videoUrl = chapter.getVideoUrl();
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    videoUrl = minioService.getSignedUrl(videoUrl);
                }

                // 创建章节VO - 使用 index 字段
                CourseIndexMobileVO chapterVo = new CourseIndexMobileVO(
                        chapter.getOrder(), // 对应 index 字段
                        chapter.getName(),
                        videoUrl
                );
                chapterList.add(chapterVo);
            }

            // 3. 创建课程详情VO - 使用 list 字段
            CourseInfoMobileVO vo = new CourseInfoMobileVO(
                    course.getCourseName(),
                    course.getCourseDescription(),
                    chapterList // 对应 list 字段
            );

            return new HttpResponseEntity<>(200, vo, "success");
        } catch (Exception e) {
            return new HttpResponseEntity<>(500, null, "服务器错误: " + e.getMessage());
        }
    }

    @GetMapping("/getPendingCourses")
    public ResponseEntity<List<Course>> getPendingCourses() {
        try {
            List<Course> pendingCourses = courseMapper.getPendingCourses();
            return ResponseEntity.ok(pendingCourses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 批准课程
    @PostMapping("/approveCourse")
    public ResponseEntity<Map<String, Object>> approveCourse(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            int courseId = (Integer) request.get("courseId");
            courseMapper.updateAuditStatus(courseId, 1); // 1=通过

            response.put("success", true);
            response.put("message", "课程已批准");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批准失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 拒绝课程
    @PostMapping("/rejectCourse")
    public ResponseEntity<Map<String, Object>> rejectCourse(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            int courseId = (Integer) request.get("courseId");
            courseMapper.updateAuditStatus(courseId, 2); // 2=拒绝

            response.put("success", true);
            response.put("message", "课程已拒绝");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "拒绝失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
