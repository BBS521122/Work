package com.work.work.dto;

import java.time.LocalDateTime;

public class ConferenceGetDTO {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private String userName;

    public ConferenceGetDTO() {
    }

    public ConferenceGetDTO(String name, LocalDateTime startTime, LocalDateTime endTime, String content, String userName) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
