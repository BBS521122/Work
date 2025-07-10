package com.work.work.entity;

public class Chapter {
    private Long id;
    private Long courseId;
    private String videoUrl;
    private String name;
    private Integer order;

    public Chapter(Long id, Long courseId, String videoUrl, String name, Integer order) {
        this.id = id;
        this.courseId = courseId;
        this.videoUrl = videoUrl;
        this.name = name;
        this.order = order;
    }

    public Chapter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    // TODO
}
