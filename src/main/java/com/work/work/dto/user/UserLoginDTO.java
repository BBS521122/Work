package com.work.work.dto.user;

import org.simpleframework.xml.core.Validate;

public class UserLoginDTO {
    private String name;
    private String password;

    public UserLoginDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public UserLoginDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
