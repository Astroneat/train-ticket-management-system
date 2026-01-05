package com.mrt.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.mrt.Universal;

public class Schedule {
    private int scheduleId;
    private int trainId;
    private int routeId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;

    public int getScheduleId() {
        return scheduleId;
    }

    public int getTrainId() {
        return trainId;
    }

    public int getRouteId() {
        return routeId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getStatus() {
        return status;
    }

    public Schedule(int scheduleId, int trainId, int routeId, LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.routeId = routeId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Schedule parseResultSet(ResultSet rs) throws SQLException {
        return new Schedule(
            rs.getInt("schedule_id"),
            rs.getInt("train_id"),
            rs.getInt("route_id"),
            toLocalDateTime(rs.getTimestamp("departure_utc")),
            toLocalDateTime(rs.getTimestamp("arrival_utc")),
            rs.getString("status")
        );
    }
}
