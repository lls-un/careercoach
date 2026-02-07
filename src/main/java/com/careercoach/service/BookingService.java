package com.careercoach.service;

import com.careercoach.entity.Booking;

import java.util.List;
import java.util.Map;

public interface BookingService {
    // 获取预约链接
    String getBookingUrl(String userId);

    // 查询用户的所有预约
    List<Booking> getMyBookings(String userId);

    // 获取取消预约链接
    String getCancelUrl(String bookingId);

    // 处理Cal.com Webhook回调
    void handleCalWebhook(Map<String, Object> webhookData);
}