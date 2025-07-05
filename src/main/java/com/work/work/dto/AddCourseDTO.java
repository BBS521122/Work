package com.work.work.dto;

public class AddCourseDTO {
    private String courseName;
    private String courseDescription;
    private String courseAuthor;
    private Integer courseSort;
    private String coverUrl;

    public AddCourseDTO(String courseName, String courseDescription, String courseAuthor, Integer courseSort, String coverUrl) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.courseAuthor = courseAuthor;
        this.courseSort = courseSort;
        this.coverUrl = coverUrl;
    }

    public AddCourseDTO() {
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
