package com.work.work.entity;

public class Tenant {

    private Long id;
    private String name;
    private String cover;
    private String contactPerson;
    private String phone;
    private String admin;
    private String note;

    public Tenant() {
    }

    public Tenant(Long id, String name, String cover, String contactPerson, String phone, String admin, String note) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.admin = admin;
        this.note = note;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
