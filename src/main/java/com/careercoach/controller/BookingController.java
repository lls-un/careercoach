package com.careercoach.controller;

import com.careercoach.entity.Booking;
import com.careercoach.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class BookingController {

    @Resource
    private BookingService bookingService;

    // 功能A：获取预约链接
    @PostMapping("/booking-url")
    public Map<String, Object> getBookingUrl(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = bookingService.getBookingUrl(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 功能B：查询我的预约
    @GetMapping("/bookings")
    public Map<String, Object> getMyBookings(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Booking> bookings = bookingService.getMyBookings(userId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", bookings);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 功能C：获取取消预约链接
    @PostMapping("/bookings/cancel")
    public Map<String, Object> getCancelUrl(@RequestParam String bookingId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = bookingService.getCancelUrl(bookingId);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", url);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 功能D：接收Cal.com Webhook回调
    @PostMapping("/webhook/cal")
    public Map<String, Object> handleCalWebhook(@RequestBody Map<String, Object> webhookData) {
        Map<String, Object> result = new HashMap<>();
        try {
            bookingService.handleCalWebhook(webhookData);
            result.put("code", 200);
            result.put("message", "success");
        } catch (Exception e) {
            log.error("处理Cal.com Webhook失败", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
}