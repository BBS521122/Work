package com.work.work.dto;

import com.work.work.enums.ConferenceStateEnum;

import java.time.LocalDateTime;

public class ConferenceWxDTO {

    private Long id;
    private String name;
    private String cover;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private String userName;

    public ConferenceWxDTO() {
    }

    public ConferenceWxDTO(Long id, String name, String cover, LocalDateTime startTime, LocalDateTime endTime, String content, String userName) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
