package com.work.work.dto;

public class TenantAddDTO {
    private String name;
    private String contactPerson;
    private String phone;
    private String admin;
    private String note;
    private String uuid;

    public TenantAddDTO() {
    }

    public TenantAddDTO(String name, String contactPerson, String phone, String admin, String note, String uuid) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.admin = admin;
        this.note = note;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
