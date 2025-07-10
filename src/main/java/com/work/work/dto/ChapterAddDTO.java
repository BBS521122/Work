// ChapterAddDTO.java
package com.work.work.dto;

import java.util.List;

public class ChapterAddDTO {
    private Long courseId;
    private List<ChapterAddItemDTO> chapters; // 修改字段名和类型

    public ChapterAddDTO() {
    }

    public ChapterAddDTO(Long courseId, List<ChapterAddItemDTO> chapters) {
        this.courseId = courseId;
        this.chapters = chapters;
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<ChapterAddItemDTO> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterAddItemDTO> chapters) {
        this.chapters = chapters;
    }
}