package com.work.work.entity;

import com.work.work.enums.ConferenceStateEnum;

import java.time.LocalDateTime;

public class Conference {
    private Long id;
    private String name;
    private ConferenceStateEnum state;
    private String cover;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private Long userId;

    public Conference(Long id, String name, ConferenceStateEnum state, String cover, LocalDateTime startTime,
                      LocalDateTime endTime, String content, Long userId) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.cover = cover;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
        this.userId = userId;
    }

    public Conference() {
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

    public ConferenceStateEnum getState() {
        return state;
    }

    public void setState(ConferenceStateEnum state) {
        this.state = state;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
