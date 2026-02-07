package com.careercoach.mapper;

import com.careercoach.entity.Booking;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BookingMapper {
    // 根据Cal.com预约ID查询
    @Select("SELECT * FROM booking WHERE booking_id = #{bookingId}")
    Booking selectByBookingId(@Param("bookingId") String bookingId);

    // 根据用户ID查询所有预约
    @Select("SELECT * FROM booking WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Booking> selectByUserId(@Param("userId") String userId);

    // 新增预约（含calUid）
    @Insert("INSERT INTO booking (booking_id, user_id, coach_name, coach_email, start_time, end_time, status, cal_booking_url, cal_cancel_url, cal_uid) " +
            "VALUES (#{bookingId}, #{userId}, #{coachName}, #{coachEmail}, #{startTime}, #{endTime}, #{status}, #{calBookingUrl}, #{calCancelUrl}, #{calUid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Booking booking);

    // 更新预约状态+取消链接+calUid（全量更新）
    @Update("UPDATE booking SET status = #{status}, cal_cancel_url = #{calCancelUrl}, cal_uid = #{calUid} WHERE booking_id = #{bookingId}")
    int updateStatusByBookingId(@Param("bookingId") String bookingId, @Param("status") String status, @Param("calCancelUrl") String calCancelUrl, @Param("calUid") String calUid);
}