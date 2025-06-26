package com.work.work.vo;

public class SettingVO {

    private String gender;
    private String email;
    private String phone;
    private String nickname;

    public SettingVO() {
    }

    public SettingVO(String gender, String email, String phone, String nickname) {
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
