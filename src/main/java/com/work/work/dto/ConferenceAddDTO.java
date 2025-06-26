package com.work.work.dto;

import com.work.work.enums.ConferenceStateEnum;

import java.time.LocalDateTime;

public class ConferenceAddDTO {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private String uuid;

    public ConferenceAddDTO() {
    }

    public ConferenceAddDTO(String name, LocalDateTime startTime, LocalDateTime endTime, String content, String uuid) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
        this.uuid = uuid;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
