package com.work.work.dto;

import java.time.LocalDateTime;

public class RequestDTO {

    private String keyword;
    private String state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public RequestDTO() {
    }

    public RequestDTO(String keyword, String state, LocalDateTime startTime, LocalDateTime endTime) {
        this.keyword = keyword;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
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
}
