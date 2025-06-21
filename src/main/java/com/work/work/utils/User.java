package com.work.work.utils;

import java.util.Date;

public class User {

    private long id;
    private String name;
    private String gender;
    private String password;
    private String state;
    private String department;
    private String email;
    private Date time;
    private String phone;
    private String role;
    private String post;
    private String photo;
    private String nickname;


    public User() {
    }

    public User(long id, String name, String gender, String password, String state, String department, String email, Date time, String phone, String role, String post, String photo, String nickname) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.password = password;
        this.state = state;
        this.department = department;
        this.email = email;
        this.time = time;
        this.phone = phone;
        this.role = role;
        this.post = post;
        this.photo = photo;
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
