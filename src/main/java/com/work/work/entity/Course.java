package com.work.work.entity;

import java.time.LocalDateTime;

public class Course {
    private Long id;
    private String courseName;
    private String courseDescription;
    private String courseAuthor;
    private Integer courseSort;
    private LocalDateTime courseCreateTime;
    private LocalDateTime courseUpdateTime;
    private String coverUrl;
    private Integer state;


    public Course(Long id, String courseName, String courseDescription, String courseAuthor, Integer courseSort, LocalDateTime courseCreateTime, LocalDateTime courseUpdateTime, String coverUrl, Integer state) {
        this.id = id;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseAuthor = courseAuthor;
        this.courseSort = courseSort;
        this.courseCreateTime = courseCreateTime;
        this.courseUpdateTime = courseUpdateTime;
        this.coverUrl = coverUrl;
        this.state = state;
    }

    public Course() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseAuthor() {
        return courseAuthor;
    }

    public void setCourseAuthor(String courseAuthor) {
        this.courseAuthor = courseAuthor;
    }

    public Integer getCourseSort() {
        return courseSort;
    }

    public void setCourseSort(Integer courseSort) {
        this.courseSort = courseSort;
    }

    public LocalDateTime getCourseCreateTime() {
        return courseCreateTime;
    }

    public void setCourseCreateTime(LocalDateTime courseCreateTime) {
        this.courseCreateTime = courseCreateTime;
    }

    public LocalDateTime getCourseUpdateTime() {
        return courseUpdateTime;
    }

    public void setCourseUpdateTime(LocalDateTime courseUpdateTime) {
        this.courseUpdateTime = courseUpdateTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
