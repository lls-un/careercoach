package com.careercoach.service.impl;

import com.careercoach.entity.Booking;
import com.careercoach.entity.User;
import com.careercoach.enums.BookingStatus;
import com.careercoach.mapper.BookingMapper;
import com.careercoach.mapper.UserMapper;
import com.careercoach.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    @Value("${cal.booking-url}")
    private String calBookingUrl;
    // Cal.com官方详情/取消页前缀（固定）
    private static final String CAL_BOOKING_DETAIL_PREFIX = "https://cal.com/bookings/";
    // 本地时区（上海）
    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private UserMapper userMapper;

    // Cal.com取消/预约页核心常量（固定不变）
    private static final String CAL_BOOKING_BASE = "https://cal.com/booking/";
    private static final String CAL_FIXED_PARAMS = "flag.coep=false&isSuccessBookingPage=true";

    // 工具方法：拼接完整的Cal.com预约/取消链接
    private String buildCalBookingUrl(String calUid, String userEmail, String eventTypeSlug) {
        // 拼接规则：base + uid + ? + 固定参数 + & + email=xxx + & + eventTypeSlug=xxx
        return new StringBuilder(CAL_BOOKING_BASE)
                .append(calUid)
                .append("?")
                .append(CAL_FIXED_PARAMS)
                .append("&email=")
                .append(userEmail)
                .append("&eventTypeSlug=")
                .append(eventTypeSlug)
                .toString();
    }

    // 功能A：获取预约链接（兼容用户自定义userId，无需从邮箱提取）
    @Override
    public String getBookingUrl(String userId) {
        // 1. 若用户不存在，创建用户（姓名/邮箱适配你的测试场景，可手动修改）
        if (userMapper.selectByUserId(userId) == null) {
            User user = new User();
            user.setUserId(userId);
            user.setName("李连帅"); // 适配你的测试姓名
            user.setEmail("lianshuaili129@gmail.com"); // 适配你的测试邮箱
            userMapper.insert(user);
            log.info("创建用户：{}，姓名：{}，邮箱：{}", userId, user.getName(), user.getEmail());
        }
        // 2. 拼接用户ID作为自定义参数，Cal.com会透传
        String finalUrl = calBookingUrl + "?userId=" + userId;
        log.info("生成预约链接：{}", finalUrl);
        return finalUrl;
    }

    // 功能B：查询我的预约（直接返回，无修改）
    @Override
    public List<Booking> getMyBookings(String userId) {
        List<Booking> bookings = bookingMapper.selectByUserId(userId);
        log.info("用户{}查询到{}条预约记录", userId, bookings.size());
        return bookings;
    }

    // 功能C：获取取消链接（核心修改：直接返回数据库中存储的完整带参链接）
    @Override
    public String getCancelUrl(String bookingId) {
        // 1. 查询预约记录
        Booking booking = bookingMapper.selectByBookingId(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在：" + bookingId);
        }
        // 2. 获取数据库中存储的完整取消链接（Webhook阶段已生成）
        String fullCancelUrl = booking.getCalCancelUrl();
        if (fullCancelUrl == null || fullCancelUrl.isEmpty()) {
            throw new RuntimeException("预约记录无完整取消链接，请先完成Cal.com预约");
        }
        log.info("生成取消链接（与Cal.com官方一致）：{}，预约ID：{}", fullCancelUrl, bookingId);
        // 3. 无需更新，直接返回（链接已在Webhook阶段存储）
        return fullCancelUrl;
    }
    // 功能D：处理Cal.com Webhook（核心修改：提取3个参数+拼接完整链接）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCalWebhook(Map<String, Object> webhookData) {
        log.info("接收Cal.com Webhook数据：{}", webhookData);

        try {
            // 1. 解析核心事件类型
            String eventType = (String) webhookData.get("triggerEvent");
            if (eventType == null || eventType.isEmpty()) {
                throw new RuntimeException("未解析到Cal.com事件类型");
            }
            // 2. 解析Payload核心数据
            Map<String, Object> payload = (Map<String, Object>) webhookData.get("payload");
            if (payload == null) {
                throw new RuntimeException("Cal.com Payload数据为空");
            }
            // 3. 解析3个核心参数（生成链接必备）
            String bookingId = payload.get("bookingId") == null ? null : payload.get("bookingId").toString(); // 数字转字符串
            String calUid = (String) payload.get("uid"); // 核心：8rPzDsLeyj9xgNECA9Zb9H
            String eventTypeSlug = (String) payload.get("type"); // 核心：careercoach
            if (bookingId == null || calUid == null || eventTypeSlug == null) {
                throw new RuntimeException("生成链接必备参数缺失：bookingId/uid/type");
            }
            // 4. 解析用户邮箱（attendees[0].email）
            List<Map<String, Object>> attendees = (List<Map<String, Object>>) payload.get("attendees");
            if (attendees == null || attendees.isEmpty()) {
                throw new RuntimeException("参会者（attendees）信息为空");
            }
            Map<String, Object> user = attendees.get(0);
            String userEmail = (String) user.get("email"); // 核心：lianshuaili129@gmail.com
            if (userEmail == null || userEmail.isEmpty()) {
                throw new RuntimeException("未解析到用户邮箱");
            }
            // 5. 拼接**完整带参的预约/取消链接**（和你要求的完全一致）
            String fullCalUrl = buildCalBookingUrl(calUid, userEmail, eventTypeSlug);
            // 6. 解析视频链接和导师信息
            String videoCallUrl = (String) payload.get("videoCallUrl");
            Map<String, Object> coach = (Map<String, Object>) payload.get("organizer");
            if (coach == null) {
                throw new RuntimeException("未解析到导师（organizer）信息");
            }
            String coachName = (String) coach.get("name");
            String coachEmail = (String) coach.get("email");
            log.info("处理Cal.com事件：{}，预约ID：{}，生成完整链接：{}", eventType, bookingId, fullCalUrl);

            // 7. 解析用户ID（适配透传参数，无则取邮箱前缀）
            String userName = (String) user.get("name");
            String userId = (String) payload.get("userId");
            if (userId == null || userId.isEmpty()) {
                userId = userEmail.split("@")[0];
            }
            log.info("解析到用户信息：姓名={}, 邮箱={}, 业务系统ID={}", userName, userEmail, userId);

            // 8. 解析预约时间（UTC转上海时区）
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            String startTimeStr = (String) payload.get("startTime");
            String endTimeStr = (String) payload.get("endTime");
            OffsetDateTime startOffset = OffsetDateTime.parse(startTimeStr, formatter);
            OffsetDateTime endOffset = OffsetDateTime.parse(endTimeStr, formatter);
            LocalDateTime startTime = startOffset.atZoneSameInstant(SHANGHAI_ZONE).toLocalDateTime();
            LocalDateTime endTime = endOffset.atZoneSameInstant(SHANGHAI_ZONE).toLocalDateTime();
            log.info("解析到预约时间（上海时区）：开始={}, 结束={}", startTime, endTime);

            // 9. 处理预约记录（新增/更新，存储完整链接）
            Booking booking = bookingMapper.selectByBookingId(bookingId);
            if (booking == null) {
                booking = new Booking();
                booking.setBookingId(bookingId);
                booking.setUserId(userId);
                booking.setCoachName(coachName);
                booking.setCoachEmail(coachEmail);
                booking.setStartTime(startTime);
                booking.setEndTime(endTime);
                booking.setStatus(BookingStatus.PENDING.getCode());
                booking.setCalBookingUrl(videoCallUrl); // 视频会议链接
                booking.setCalUid(calUid); // 存储uid
                booking.setCalCancelUrl(fullCalUrl); // 存储**完整带参的取消链接**
            }

            // 10. 根据事件类型更新状态
            if ("BOOKING_CREATED".equals(eventType)) {
                booking.setStatus(BookingStatus.BOOKING_CREATED.getCode());
                bookingMapper.insert(booking);
                log.info("✅ 预约创建成功：{}，用户：{}，取消链接：{}", bookingId, userId, fullCalUrl);
            } else if ("BOOKING_CANCELLED".equals(eventType)) {
                booking.setStatus(BookingStatus.BOOKING_CANCELLED.getCode());
                bookingMapper.updateStatusByBookingId(bookingId, booking.getStatus(), fullCalUrl, calUid);
                log.info("✅ 预约取消成功：{}，取消链接：{}", bookingId, fullCalUrl);
            } else {
                log.warn("⚠️ 未处理的Cal.com事件类型：{}", eventType);
            }

        } catch (Exception e) {
            log.error("❌ 解析Cal.com Webhook数据失败", e);
            throw new RuntimeException("处理Webhook失败：" + e.getMessage());
        }
    }
}