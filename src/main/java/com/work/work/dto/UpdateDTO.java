package com.work.work.dto;

public class UpdateDTO {

    private String nickname;
    private String phone;
    private String email;
    private String gender;

    public UpdateDTO() {
    }

    public UpdateDTO(String nickname, String phone, String email, String gender) {
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
