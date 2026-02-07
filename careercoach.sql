create table booking
(
    id              bigint auto_increment comment '主键'
        primary key,
    booking_id      varchar(64)                        not null comment 'Cal.com 预约ID',
    user_id         varchar(64)                        not null comment '用户ID',
    coach_name      varchar(128)                       not null comment '导师姓名',
    coach_email     varchar(128)                       not null comment '导师邮箱',
    start_time      datetime                           not null comment '预约开始时间',
    end_time        datetime                           not null comment '预约结束时间',
    status          varchar(32)                        not null comment '预约状态：PENDING/BOOKING_CREATED/BOOKING_CANCELLED/MEETING_ENDED/NO_SHOW',
    cal_booking_url varchar(256)                       null comment 'Cal.com 预约链接',
    cal_cancel_url  varchar(256)                       null comment 'Cal.com 取消链接',
    cal_uid         varchar(128)                       null comment 'Cal.com预约唯一标识（用于生成详情/取消页）',
    create_time     datetime default CURRENT_TIMESTAMP null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_booking_id
        unique (booking_id)
)
    comment '预约记录表';

create index idx_cal_uid
    on booking (cal_uid);

create index idx_status
    on booking (status);

create index idx_user_id
    on booking (user_id);

create table user
(
    user_id     varchar(64)                        not null comment '用户唯一ID'
        primary key,
    name        varchar(128)                       not null comment '姓名',
    email       varchar(128)                       not null comment '邮箱',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint uk_email
        unique (email)
)
    comment '用户表';


