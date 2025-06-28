package com.work.work.vo;

import com.work.work.enums.RoleEnum;

public class UserLoginVO {
    private Long id;
    private String name;
    private String token;
    private RoleEnum role;

    public UserLoginVO() {
    }

    public UserLoginVO(Long id, String name, String token, RoleEnum role) {
        this.id = id;
        this.name = name;
        this.token = token;
        this.role = role;
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
}

