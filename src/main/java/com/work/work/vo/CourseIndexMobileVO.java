package com.work.work.vo;

public class CourseIndexMobileVO {
    private int index;
    private String name;
    private String videoUrl;

    public CourseIndexMobileVO() {
    }

    public CourseIndexMobileVO(int index, String name, String videoUrl) {
        this.index = index;
        this.name = name;
        this.videoUrl = videoUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
