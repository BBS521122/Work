package com.work.work.dto;


public class TenantUpdateDTO {
    private Long id;
    private String name;
    private String note;
    private String uuid;

    public TenantUpdateDTO() {
    }

    public TenantUpdateDTO(Long id, String name, String note, String uuid) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.uuid = uuid;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
