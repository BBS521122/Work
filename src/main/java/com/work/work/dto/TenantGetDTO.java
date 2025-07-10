package com.work.work.dto;

public class TenantGetDTO {
    private String name;
    private String contactPerson;
    private String phone;
    private String admin;
    private String note;

    public TenantGetDTO() {
    }

    public TenantGetDTO(String name, String contactPerson, String phone, String admin, String note) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.admin = admin;
        this.note = note;
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
}
