package com.careercoach.enums;

public enum BookingStatus {
    PENDING("PENDING", "初始状态"),
    BOOKING_CREATED("BOOKING_CREATED", "支付成功且预约确认"),
    BOOKING_CANCELLED("BOOKING_CANCELLED", "预约已取消"),
    MEETING_ENDED("MEETING_ENDED", "课程正常结束"),
    NO_SHOW("NO_SHOW", "未出席");

    private final String code;
    private final String desc;

    BookingStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}