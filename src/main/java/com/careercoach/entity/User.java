package com.careercoach.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String userId;       // 用户唯一ID
    private String name;        // 姓名
    private String email;       // 邮箱
    private LocalDateTime createTime;  // 创建时间
}