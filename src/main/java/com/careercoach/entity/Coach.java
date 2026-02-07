package com.careercoach.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Coach {
    private Long coachId;
    private String calCoachId;
    private String name;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}