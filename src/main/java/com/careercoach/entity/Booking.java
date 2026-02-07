package com.careercoach.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;                // 主键
    private String bookingId;       // Cal.com的bookingId（数字）
    private String userId;          // 业务系统用户ID
    private String coachName;       // 导师姓名
    private String coachEmail;      // 导师邮箱
    private LocalDateTime startTime; // 预约开始时间（上海时区）
    private LocalDateTime endTime;   // 预约结束时间（上海时区）
    private String status;          // 预约状态
    private String calBookingUrl;   // Cal.com预约视频链接
    private String calCancelUrl;    // Cal.com取消链接（详情页）
    private String calUid;          // 新增：Cal.com的uid（核心，生成取消页）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}