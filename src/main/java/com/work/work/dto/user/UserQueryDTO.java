package com.work.work.dto.user;

import java.util.Date;

public class UserQueryDTO {
    private String name;
    private String phone;
    private String state;
    private Date startDate;
    private Date endDate;

    public UserQueryDTO() {
    }

    public UserQueryDTO(String name, String phone, String state, Date startDate, Date endDate) {
        this.name = name;
        this.phone = phone;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
