package com.work.work.frontendtest;

public class CourseOverviewMobileVO {
    private Long id;
    private String coverUrl;
    private String title;
    private String author;

    public CourseOverviewMobileVO() {
    }

    public CourseOverviewMobileVO(Long id, String coverUrl, String title, String author) {
        this.id = id;
        this.coverUrl = coverUrl;
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
