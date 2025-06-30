package com.work.work.frontendtest;

public class CourseOverviewMobileVO {
    private Long id;
    private String coverUrl;
    private String title;
    private String creator;

    public CourseOverviewMobileVO() {
    }

    public CourseOverviewMobileVO(Long id, String coverUrl, String title, String creator) {
        this.id = id;
        this.coverUrl = coverUrl;
        this.title = title;
        this.creator = creator;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
