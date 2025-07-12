package com.work.work.controller;

import com.work.work.dto.AddCourseDTO;
import com.work.work.dto.ChapterAddDTO;
import com.work.work.dto.ChapterAddItemDTO;
import com.work.work.entity.Chapter;
import com.work.work.entity.Course;
import com.work.work.mapper.ChapterMapper;
import com.work.work.mapper.CourseMapper;
import com.work.work.properties.MinioProperties;
import com.work.work.service.CourseService;
import com.work.work.service.MinioService;
import com.work.work.vo.CourseInfoMobileVO;
import com.work.work.vo.CourseOverviewMobileVO;
import com.work.work.vo.HttpResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private MinioService minioService;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private ChapterMapper chapterMapper;

    @Mock
    private CourseService courseService;

    @Mock
    private MinioProperties minioProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCourse_Success() {
        AddCourseDTO addCourseDTO = new AddCourseDTO();
        addCourseDTO.setCourseName("Test Course");
        addCourseDTO.setCourseDescription("Description");
        addCourseDTO.setCourseAuthor("Author");
        addCourseDTO.setCoverUrl("cover.jpg");

        when(courseMapper.addCourse(any(Course.class))).thenAnswer(invocation -> {
            Course course = invocation.getArgument(0);
            course.setId(1L);
            return 1;
        });

        HttpResponseEntity<Long> response = courseController.addCourse(addCourseDTO);

        assertEquals(200, response.getCode());
        assertEquals(1L, response.getData());
        assertEquals("课程保存成功", response.getMessage());
    }

    @Test
    void addCourse_Failure() {
        AddCourseDTO addCourseDTO = new AddCourseDTO();
        addCourseDTO.setCourseName("Test Course");

        when(courseMapper.addCourse(any(Course.class))).thenReturn(0);

        HttpResponseEntity<Long> response = courseController.addCourse(addCourseDTO);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertEquals("保存课程失败", response.getMessage());
        verify(courseMapper, times(1)).addCourse(any(Course.class));
    }

    @Test
    void addCourse_Exception() {
        AddCourseDTO addCourseDTO = new AddCourseDTO();
        addCourseDTO.setCourseName("Test Course");

        when(courseMapper.addCourse(any(Course.class))).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<Long> response = courseController.addCourse(addCourseDTO);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
        verify(courseMapper, times(1)).addCourse(any(Course.class));
    }

    @Test
    void addUpdateCourse_Success_NoVideoUrlManipulation() {
        ChapterAddDTO chapterAddDTO = new ChapterAddDTO();
        chapterAddDTO.setCourseId(1L);
        ChapterAddItemDTO item1 = new ChapterAddItemDTO();
        item1.setName("Chapter 1");
        item1.setOrder(1);
        item1.setVideoUrl("video1.mp4");
        ChapterAddItemDTO item2 = new ChapterAddItemDTO();
        item2.setName("Chapter 2");
        item2.setOrder(2);
        item2.setVideoUrl("video2.mp4");
        chapterAddDTO.setChapters(Arrays.asList(item1, item2));

        when(minioProperties.getBucket()).thenReturn("testbucket");
        when(chapterMapper.deleteChaptersByCourseId(anyLong())).thenReturn(1);
        when(chapterMapper.insertChapter(any(Chapter.class))).thenReturn(1);
        when(courseMapper.updateTimeById(anyLong(), any(LocalDateTime.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.addUpdateCourse(chapterAddDTO);

        assertEquals(200, response.getCode());
        assertEquals(2, response.getData());
        assertEquals("课程章节更新成功", response.getMessage());
        verify(chapterMapper, times(1)).deleteChaptersByCourseId(1L);
        verify(chapterMapper, times(2)).insertChapter(any(Chapter.class));
        verify(courseMapper, times(1)).updateTimeById(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void addUpdateCourse_Success_WithVideoUrlManipulation_FullUrl() {
        ChapterAddDTO chapterAddDTO = new ChapterAddDTO();
        chapterAddDTO.setCourseId(1L);
        ChapterAddItemDTO item1 = new ChapterAddItemDTO();
        item1.setName("Chapter 1");
        item1.setOrder(1);
        item1.setVideoUrl("http://localhost:9000/testbucket/path/to/video.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256");
        chapterAddDTO.setChapters(Collections.singletonList(item1));

        when(minioProperties.getBucket()).thenReturn("testbucket");

        // 修复：使用 thenReturn() 替代 doNothing()
        when(chapterMapper.deleteChaptersByCourseId(anyLong())).thenReturn(1); // 模拟删除操作返回1行受影响
        when(courseMapper.updateTimeById(anyLong(), any(LocalDateTime.class))).thenReturn(1); // 模拟更新操作返回1行受影响

        when(chapterMapper.insertChapter(any(Chapter.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.addUpdateCourse(chapterAddDTO);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程章节更新成功", response.getMessage());
        verify(chapterMapper, times(1)).deleteChaptersByCourseId(1L);
        verify(chapterMapper, times(1)).insertChapter(argThat(chapter ->
                chapter.getVideoUrl().equals("path/to/video.mp4")));
        verify(courseMapper, times(1)).updateTimeById(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void addUpdateCourse_Success_WithVideoUrlManipulation_NoQuestionMark() {
        ChapterAddDTO chapterAddDTO = new ChapterAddDTO();
        chapterAddDTO.setCourseId(1L);
        ChapterAddItemDTO item1 = new ChapterAddItemDTO();
        item1.setName("Chapter 1");
        item1.setOrder(1);
        item1.setVideoUrl("http://localhost:9000/testbucket/path/to/video.mp4");
        chapterAddDTO.setChapters(Collections.singletonList(item1));

        when(minioProperties.getBucket()).thenReturn("testbucket");

        // 修复：使用 thenReturn() 替代 doNothing()
        when(chapterMapper.deleteChaptersByCourseId(anyLong())).thenReturn(1); // 模拟删除操作返回1行受影响
        when(courseMapper.updateTimeById(anyLong(), any(LocalDateTime.class))).thenReturn(1); // 模拟更新操作返回1行受影响

        when(chapterMapper.insertChapter(any(Chapter.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.addUpdateCourse(chapterAddDTO);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程章节更新成功", response.getMessage());
        verify(chapterMapper, times(1)).deleteChaptersByCourseId(1L);
        verify(chapterMapper, times(1)).insertChapter(argThat(chapter ->
                chapter.getVideoUrl().equals("path/to/video.mp4")));
        verify(courseMapper, times(1)).updateTimeById(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void addUpdateCourse_Success_WithVideoUrlManipulation_NoBucketInUrl() {
        ChapterAddDTO chapterAddDTO = new ChapterAddDTO();
        chapterAddDTO.setCourseId(1L);
        ChapterAddItemDTO item1 = new ChapterAddItemDTO();
        item1.setName("Chapter 1");
        item1.setOrder(1);
        item1.setVideoUrl("http://localhost:9000/anotherbucket/path/to/video.mp4");
        chapterAddDTO.setChapters(Collections.singletonList(item1));

        when(minioProperties.getBucket()).thenReturn("testbucket");

        // 修复：使用 thenReturn() 替代 doNothing()
        when(chapterMapper.deleteChaptersByCourseId(anyLong())).thenReturn(1); // 模拟删除操作返回1行受影响
        when(courseMapper.updateTimeById(anyLong(), any(LocalDateTime.class))).thenReturn(1); // 模拟更新操作返回1行受影响
        when(chapterMapper.insertChapter(any(Chapter.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.addUpdateCourse(chapterAddDTO);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程章节更新成功", response.getMessage());
        verify(chapterMapper, times(1)).deleteChaptersByCourseId(1L);
        verify(chapterMapper, times(1)).insertChapter(argThat(chapter ->
                chapter.getVideoUrl().equals("http://localhost:9000/anotherbucket/path/to/video.mp4")));
        verify(courseMapper, times(1)).updateTimeById(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void addUpdateCourse_Exception() {
        ChapterAddDTO chapterAddDTO = new ChapterAddDTO();
        chapterAddDTO.setCourseId(1L);
        chapterAddDTO.setChapters(new ArrayList<>());

        doThrow(new RuntimeException("DB Error")).when(chapterMapper).deleteChaptersByCourseId(anyLong());

        HttpResponseEntity<Integer> response = courseController.addUpdateCourse(chapterAddDTO);

        assertEquals(500, response.getCode());
        assertEquals(0, response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
        verify(chapterMapper, times(1)).deleteChaptersByCourseId(1L);
        verify(chapterMapper, never()).insertChapter(any(Chapter.class));
    }

    @Test
    void upload_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "video content".getBytes());
        String expectedUuid = "uuid-123";

        when(courseService.updateCourse(file)).thenReturn(expectedUuid);

        HttpResponseEntity<String> response = courseController.upload(file);

        assertEquals(200, response.getCode());
        assertEquals(expectedUuid, response.getData());
        assertEquals("success", response.getMessage());
        verify(courseService, times(1)).updateCourse(file);
    }

    @Test
    void allCourse_Success() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCoverUrl("cover1.jpg");
        Course course2 = new Course();
        course2.setId(2L);
        course2.setCoverUrl("cover2.jpg");
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseMapper.getallCourse()).thenReturn(courses);
        when(minioService.getSignedUrl("cover1.jpg")).thenReturn("signed_cover1.jpg");
        when(minioService.getSignedUrl("cover2.jpg")).thenReturn("signed_cover2.jpg");

        HttpResponseEntity<List<Course>> response = courseController.allCourse();

        assertEquals(200, response.getCode());
        assertEquals(2, response.getData().size());
        assertEquals("signed_cover1.jpg", response.getData().get(0).getCoverUrl());
        assertEquals("signed_cover2.jpg", response.getData().get(1).getCoverUrl());
        assertEquals("success", response.getMessage());
        verify(courseMapper, times(1)).getallCourse();
        verify(minioService, times(1)).getSignedUrl("cover1.jpg");
        verify(minioService, times(1)).getSignedUrl("cover2.jpg");
    }

    @Test
    void getCourseInfo_Success() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setCoverUrl("original_cover.jpg");

        when(courseMapper.getCourseById(courseId)).thenReturn(course);
        when(minioService.getSignedUrl("original_cover.jpg")).thenReturn("signed_cover.jpg");

        HttpResponseEntity<Course> response = courseController.getCourseInfo(courseId);

        assertEquals(200, response.getCode());
        assertEquals(courseId, response.getData().getId());
        assertEquals("signed_cover.jpg", response.getData().getCoverUrl());
        assertEquals("success", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, times(1)).getSignedUrl("original_cover.jpg");
    }

    @Test
    void getCourseInfo_NotFound() {
        Long courseId = 1L;
        when(courseMapper.getCourseById(courseId)).thenReturn(null);

        HttpResponseEntity<Course> response = courseController.getCourseInfo(courseId);

        assertEquals(404, response.getCode());
        assertNull(response.getData());
        assertEquals("课程不存在", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, never()).getSignedUrl(anyString());
    }

    @Test
    void getCourseInfo_Exception() {
        Long courseId = 1L;
        when(courseMapper.getCourseById(courseId)).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<Course> response = courseController.getCourseInfo(courseId);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, never()).getSignedUrl(anyString());
    }

    @Test
    void deleteCourse_Success_WithCover() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setCoverUrl("cover.jpg");

        when(courseMapper.getCourseById(courseId)).thenReturn(course);
        doNothing().when(minioService).deleteFile(anyString());
        when(courseMapper.deleteCourses(anyList())).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.deleteCourse(courseId);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程删除成功", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, times(1)).deleteFile("cover.jpg");
        verify(courseMapper, times(1)).deleteCourses(Collections.singletonList(courseId));
    }

    @Test
    void deleteCourse_Success_NoCover() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setCoverUrl(null);

        when(courseMapper.getCourseById(courseId)).thenReturn(course);
        when(courseMapper.deleteCourses(anyList())).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.deleteCourse(courseId);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程删除成功", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, never()).deleteFile(anyString());
        verify(courseMapper, times(1)).deleteCourses(Collections.singletonList(courseId));
    }

    @Test
    void deleteCourse_InvalidId() {
        HttpResponseEntity<Integer> response = courseController.deleteCourse(0L);

        assertEquals(400, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("无效的课程ID", response.getMessage());
        verify(courseMapper, never()).getCourseById(anyLong());
    }

    @Test
    void deleteCourse_NotFound() {
        Long courseId = 1L;
        when(courseMapper.getCourseById(courseId)).thenReturn(null);

        HttpResponseEntity<Integer> response = courseController.deleteCourse(courseId);

        assertEquals(404, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("课程不存在", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, never()).deleteFile(anyString());
        verify(courseMapper, never()).deleteCourses(anyList());
    }

    @Test
    void deleteCourse_DeletionFailure() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setCoverUrl("cover.jpg");

        when(courseMapper.getCourseById(courseId)).thenReturn(course);
        doNothing().when(minioService).deleteFile(anyString());
        when(courseMapper.deleteCourses(anyList())).thenReturn(0);

        HttpResponseEntity<Integer> response = courseController.deleteCourse(courseId);

        assertEquals(500, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("课程删除失败", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, times(1)).deleteFile("cover.jpg");
        verify(courseMapper, times(1)).deleteCourses(Collections.singletonList(courseId));
    }

    @Test
    void deleteCourse_Exception() {
        Long courseId = 1L;
        when(courseMapper.getCourseById(courseId)).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<Integer> response = courseController.deleteCourse(courseId);

        assertEquals(500, response.getCode());
        assertEquals(0, response.getData());
        assertTrue(response.getMessage().contains("删除失败"));
        verify(courseMapper, times(1)).getCourseById(courseId);
        verify(minioService, never()).deleteFile(anyString());
        verify(courseMapper, never()).deleteCourses(anyList());
    }

    @Test
    void updateCourse_Success_NewCover() {
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setCoverUrl("old_cover.jpg");

        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setCourseName("Updated Name");
        updatedCourse.setCoverUrl("new_cover.jpg");

        when(courseMapper.getCourseById(1L)).thenReturn(existingCourse);
        doNothing().when(minioService).deleteFile("old_cover.jpg");
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.updateCourse(updatedCourse);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程更新成功", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(1L);
        verify(minioService, times(1)).deleteFile("old_cover.jpg");
        verify(courseMapper, times(1)).updateCourse(argThat(course ->
                course.getCourseName().equals("Updated Name") &&
                        course.getCoverUrl().equals("new_cover.jpg") &&
                        course.getCourseUpdateTime() != null));
    }

    @Test
    void updateCourse_Success_NoCoverChange() {
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setCoverUrl("old_cover.jpg");

        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setCourseName("Updated Name");
        updatedCourse.setCoverUrl(null); // No new cover provided

        when(courseMapper.getCourseById(1L)).thenReturn(existingCourse);
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(1);

        HttpResponseEntity<Integer> response = courseController.updateCourse(updatedCourse);

        assertEquals(200, response.getCode());
        assertEquals(1, response.getData());
        assertEquals("课程更新成功", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(1L);
        verify(minioService, never()).deleteFile(anyString()); // Old cover should not be deleted
        verify(courseMapper, times(1)).updateCourse(argThat(course ->
                course.getCourseName().equals("Updated Name") &&
                        course.getCoverUrl().equals("old_cover.jpg") && // Old cover retained
                        course.getCourseUpdateTime() != null));
    }

    @Test
    void updateCourse_NotFound() {
        Course updatedCourse = new Course();
        updatedCourse.setId(1L);

        when(courseMapper.getCourseById(1L)).thenReturn(null);

        HttpResponseEntity<Integer> response = courseController.updateCourse(updatedCourse);

        assertEquals(404, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("未找到课程信息", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(1L);
        verify(minioService, never()).deleteFile(anyString());
        verify(courseMapper, never()).updateCourse(any(Course.class));
    }

    @Test
    void updateCourse_UpdateFailure() {
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setCoverUrl("old_cover.jpg");

        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setCoverUrl("new_cover.jpg");

        when(courseMapper.getCourseById(1L)).thenReturn(existingCourse);
        doNothing().when(minioService).deleteFile(anyString());
        when(courseMapper.updateCourse(any(Course.class))).thenReturn(0);

        HttpResponseEntity<Integer> response = courseController.updateCourse(updatedCourse);

        assertEquals(500, response.getCode());
        assertEquals(0, response.getData());
        assertEquals("更新课程失败", response.getMessage());
        verify(courseMapper, times(1)).getCourseById(1L);
        verify(minioService, times(1)).deleteFile("old_cover.jpg");
        verify(courseMapper, times(1)).updateCourse(any(Course.class));
    }

    @Test
    void updateCourse_Exception() {
        Course updatedCourse = new Course();
        updatedCourse.setId(1L);

        when(courseMapper.getCourseById(1L)).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<Integer> response = courseController.updateCourse(updatedCourse);

        assertEquals(500, response.getCode());
        assertEquals(0, response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
        verify(courseMapper, times(1)).getCourseById(1L);
        verify(minioService, never()).deleteFile(anyString());
        verify(courseMapper, never()).updateCourse(any(Course.class));
    }

    @Test
    void getChapter_Success() {
        Long courseId = 1L;
        Chapter chapter1 = new Chapter();
        chapter1.setVideoUrl("video1.mp4");
        Chapter chapter2 = new Chapter();
        chapter2.setVideoUrl(null);
        List<Chapter> chapters = Arrays.asList(chapter1, chapter2);

        when(chapterMapper.getChaptersByCourseId(courseId)).thenReturn(chapters);
        when(minioService.getSignedUrl("video1.mp4")).thenReturn("signed_video1.mp4");

        HttpResponseEntity<List<Chapter>> response = courseController.getChapter(courseId);

        assertEquals(200, response.getCode());
        assertEquals(2, response.getData().size());
        assertEquals("signed_video1.mp4", response.getData().get(0).getVideoUrl());
        assertNull(response.getData().get(1).getVideoUrl());
        assertEquals("章节获取成功", response.getMessage());
        verify(chapterMapper, times(1)).getChaptersByCourseId(courseId);
        verify(minioService, times(1)).getSignedUrl("video1.mp4");
    }

    @Test
    void getChapter_Exception() {
        Long courseId = 1L;
        when(chapterMapper.getChaptersByCourseId(courseId)).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<List<Chapter>> response = courseController.getChapter(courseId);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
        verify(chapterMapper, times(1)).getChaptersByCourseId(courseId);
        verify(minioService, never()).getSignedUrl(anyString());
    }


    @Test
    void getPendingCourses_Success() {
        Course course1 = new Course();
        course1.setId(1L);
        List<Course> pendingCourses = Collections.singletonList(course1);

        when(courseMapper.getPendingCourses()).thenReturn(pendingCourses);

        ResponseEntity<List<Course>> response = courseController.getPendingCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        verify(courseMapper, times(1)).getPendingCourses();
    }

    @Test
    void getPendingCourses_Exception() {
        when(courseMapper.getPendingCourses()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<Course>> response = courseController.getPendingCourses();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(courseMapper, times(1)).getPendingCourses();
    }

    @Test
    void approveCourse_Success() {
        Long courseId = 1L;
        Map<String, Object> request = Collections.singletonMap("courseId", courseId.intValue());

        when(courseMapper.updateAuditStatus(courseId.intValue(), 1)).thenReturn(1);

        ResponseEntity<Map<String, Object>> response = courseController.approveCourse(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("课程已批准", response.getBody().get("message"));
        verify(courseMapper, times(1)).updateAuditStatus(courseId.intValue(), 1);
    }

    @Test
    void approveCourse_Exception() {
        Long courseId = 1L;
        Map<String, Object> request = Collections.singletonMap("courseId", courseId.intValue());

        when(courseMapper.updateAuditStatus(courseId.intValue(), 1)).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<Map<String, Object>> response = courseController.approveCourse(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertTrue(response.getBody().get("message").toString().contains("批准失败"));
        verify(courseMapper, times(1)).updateAuditStatus(courseId.intValue(), 1);
    }

    @Test
    void rejectCourse_Success() {
        Long courseId = 1L;
        Map<String, Object> request = Collections.singletonMap("courseId", courseId.intValue());

        when(courseMapper.updateAuditStatus(courseId.intValue(), 2)).thenReturn(1);

        ResponseEntity<Map<String, Object>> response = courseController.rejectCourse(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("课程已拒绝", response.getBody().get("message"));
        verify(courseMapper, times(1)).updateAuditStatus(courseId.intValue(), 2);
    }

    @Test
    void rejectCourse_Exception() {
        Long courseId = 1L;
        Map<String, Object> request = Collections.singletonMap("courseId", courseId.intValue());

        when(courseMapper.updateAuditStatus(courseId.intValue(), 2)).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<Map<String, Object>> response = courseController.rejectCourse(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertTrue(response.getBody().get("message").toString().contains("拒绝失败"));
        verify(courseMapper, times(1)).updateAuditStatus(courseId.intValue(), 2);
    }

    @Test
    void getMobileCourses_Success() {
        // 准备测试数据
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCourseName("Test Course 1");
        course1.setCourseDescription("Description 1");
        course1.setCourseAuthor("Author 1");
        course1.setCoverUrl("cover1.jpg");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setCourseName("Test Course 2");
        course2.setCourseDescription("Description 2");
        course2.setCourseAuthor("Author 2");
        course2.setCoverUrl(null);

        when(courseMapper.getMobileCourses()).thenReturn(Arrays.asList(course1, course2));
        when(minioService.getSignedUrl("cover1.jpg")).thenReturn("signed_cover1.jpg");

        // 测试不带查询条件
        HttpResponseEntity<List<CourseOverviewMobileVO>> response = courseController.getMobileCourses("");

        assertEquals(200, response.getCode());
        assertEquals(2, response.getData().size());
        assertEquals("signed_cover1.jpg", response.getData().get(0).getCoverUrl());
        assertNull(response.getData().get(1).getCoverUrl());
        assertEquals("success", response.getMessage());

        // 测试带查询条件
        response = courseController.getMobileCourses("test");
        assertEquals(200, response.getCode());
        assertTrue(response.getData().size() <= 2);
    }

    @Test
    void getMobileCourses_Exception() {
        when(courseMapper.getMobileCourses()).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<List<CourseOverviewMobileVO>> response = courseController.getMobileCourses("");

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
    }

    @Test
    void getCourseInfoMobile_Success() {
        Long courseId = 1L;

        // 准备课程数据
        Course course = new Course();
        course.setId(courseId);
        course.setCourseName("Test Course");
        course.setCourseDescription("Description");

        // 准备章节数据
        Chapter chapter1 = new Chapter();
        chapter1.setOrder(1);
        chapter1.setName("Chapter 1");
        chapter1.setVideoUrl("video1.mp4");

        Chapter chapter2 = new Chapter();
        chapter2.setOrder(2);
        chapter2.setName("Chapter 2");
        chapter2.setVideoUrl(null);

        when(courseMapper.getMobileCourseInfo(courseId)).thenReturn(course);
        when(chapterMapper.getChaptersByCourseId(courseId)).thenReturn(Arrays.asList(chapter1, chapter2));
        when(minioService.getSignedUrl("video1.mp4")).thenReturn("signed_video1.mp4");

        HttpResponseEntity<CourseInfoMobileVO> response = courseController.getCourseInfoMobile(courseId);

        assertEquals(200, response.getCode());
        assertEquals("Test Course", response.getData().getTitle());
        assertEquals(2, response.getData().getList().size());
        assertEquals("signed_video1.mp4", response.getData().getList().get(0).getVideoUrl());
        assertNull(response.getData().getList().get(1).getVideoUrl());
        assertEquals("success", response.getMessage());
    }

    @Test
    void getCourseInfoMobile_NotFound() {
        Long courseId = 1L;
        when(courseMapper.getMobileCourseInfo(courseId)).thenReturn(null);

        HttpResponseEntity<CourseInfoMobileVO> response = courseController.getCourseInfoMobile(courseId);

        assertEquals(404, response.getCode());
        assertNull(response.getData());
        assertEquals("课程不存在或未通过审核", response.getMessage());
    }

    @Test
    void getCourseInfoMobile_Exception() {
        Long courseId = 1L;
        when(courseMapper.getMobileCourseInfo(courseId)).thenThrow(new RuntimeException("DB Error"));

        HttpResponseEntity<CourseInfoMobileVO> response = courseController.getCourseInfoMobile(courseId);

        assertEquals(500, response.getCode());
        assertNull(response.getData());
        assertTrue(response.getMessage().contains("服务器错误"));
    }

}
