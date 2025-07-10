package com.work.work.dto;

public class ReceiptFormDTO {
    public Long id;
    public String unit;
    public String name;
    public String gender;
    public String phone;
    public String email;
    public String roomType;
    public String arrivalMethod;
    public String arrivalTrain;
    public String arrivalTime;
    public String returnMethod;
    public String returnTrain;
    public String returnTime;
    public String remarks;

    public ReceiptFormDTO() {
    }

    public ReceiptFormDTO(Long id, String unit, String name, String gender, String phone, String email, String roomType, String arrivalMethod, String arrivalTrain, String arrivalTime, String returnMethod, String returnTrain, String returnTime, String remarks) {
        this.id = id;
        this.unit = unit;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.roomType = roomType;
        this.arrivalMethod = arrivalMethod;
        this.arrivalTrain = arrivalTrain;
        this.arrivalTime = arrivalTime;
        this.returnMethod = returnMethod;
        this.returnTrain = returnTrain;
        this.returnTime = returnTime;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getArrivalMethod() {
        return arrivalMethod;
    }

    public void setArrivalMethod(String arrivalMethod) {
        this.arrivalMethod = arrivalMethod;
    }

    public String getArrivalTrain() {
        return arrivalTrain;
    }

    public void setArrivalTrain(String arrivalTrain) {
        this.arrivalTrain = arrivalTrain;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getReturnMethod() {
        return returnMethod;
    }

    public void setReturnMethod(String returnMethod) {
        this.returnMethod = returnMethod;
    }

    public String getReturnTrain() {
        return returnTrain;
    }

    public void setReturnTrain(String returnTrain) {
        this.returnTrain = returnTrain;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
