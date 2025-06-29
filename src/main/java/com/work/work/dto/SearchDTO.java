package com.work.work.dto;

public class SearchDTO {

    private Long id;
    private String contact;
    private String phone;
    private String name;

    public SearchDTO() {
    }

    public SearchDTO(Long id, String contact, String phone, String name) {
        this.id = id;
        this.contact = contact;
        this.phone = phone;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
