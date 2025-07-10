package com.work.work.dto.user;

public class UpdatePasswordDTO {
    private String password;

    public UpdatePasswordDTO() {
    }

    public UpdatePasswordDTO(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}