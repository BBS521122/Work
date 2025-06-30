package com.work.work.vo;

import com.work.work.enums.RoleEnum;

public class UserLoginVO {
    private Long id;
    private String name;
    private String token;
    private RoleEnum role;
    private String state;

    public UserLoginVO() {
    }

    public UserLoginVO(Long id, String name, String token, RoleEnum role, String state) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.role = role;
        this.state = state;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

