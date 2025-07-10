package com.work.work.entity;

public class ConferenceRecord {
    private Long id;
    private Long ConferenceId;
    private String video;
    private String text;

    public ConferenceRecord() {
    }



    public ConferenceRecord(Long id, Long conferenceId, String video, String text) {
        this.id = id;
        ConferenceId = conferenceId;
        this.video = video;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConferenceId() {
        return ConferenceId;
    }

    public void setConferenceId(Long conferenceId) {
        ConferenceId = conferenceId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
