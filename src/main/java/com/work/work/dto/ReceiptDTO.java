package com.work.work.dto;

public class ReceiptDTO {

    private String name;
    private String unit;
    private String gender;
    private String phone;
    private String email;
    private String conferenceName;

    public ReceiptDTO() {
    }

    public ReceiptDTO(String name, String unit, String gender, String phone, String email, String conferenceName) {
        this.name = name;
        this.unit = unit;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.conferenceName = conferenceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }
}
