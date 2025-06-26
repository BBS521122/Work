package com.work.work.entity;

public class ConferenceMedia {
    private Long id;
    private String uuid;
    private String conferenceId;
    private String name;

    public ConferenceMedia() {
    }

    public ConferenceMedia(Long id, String uuid, String conferenceId, String name) {
        this.id = id;
        this.uuid = uuid;
        this.conferenceId = conferenceId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
