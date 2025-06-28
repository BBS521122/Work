package com.work.work.dto;

import com.work.work.enums.ConferenceStateEnum;

import java.time.LocalDateTime;

public class ConferenceGettingDTO {
    private Long id;
    private String name;
    private ConferenceStateEnum state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    private String userName;

    public ConferenceGettingDTO() {
    }

    public ConferenceGettingDTO(Long id, String name, ConferenceStateEnum state, LocalDateTime startTime, LocalDateTime endTime, String content, String userName) {
        this.id = id;
        this.name = name;
        this.state = state;
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

    public ConferenceStateEnum getState() {
        return state;
    }

    public void setState(ConferenceStateEnum state) {
        this.state = state;
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