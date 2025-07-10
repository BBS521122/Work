package com.work.work.enums;

public enum ConferenceStateEnum {
    UNDER_CHECK("UNDER_CHECK"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    ONGOING("ONGOING"),
    COMPLETED("COMPLETED");

    private final String dbValue;

    ConferenceStateEnum(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ConferenceStateEnum fromDbValue(String dbValue) {
        for (ConferenceStateEnum state : values()) {
            if (state.dbValue.equals(dbValue)) {
                return state;
            }
        }
        throw new IllegalArgumentException("未知的会议状态: " + dbValue);
    }
}