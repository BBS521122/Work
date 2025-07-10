package com.work.work.dto;

public class ChapterAddItemDTO {
    private String videoUrl;
    private Integer order;
    private String name;
    // TODO


    public ChapterAddItemDTO(String videoUrl, Integer order, String name) {
        this.videoUrl = videoUrl;
        this.order = order;
        this.name = name;
    }

    public ChapterAddItemDTO() {
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
