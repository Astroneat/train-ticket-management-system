package com.mrt.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Schedule {
    private int scheduleId;
    private int trainId;
    private int routeId;
    private LocalDateTime departure_time;
    private LocalDateTime arrival_time;
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

    public LocalDateTime getDeparture_time() {
        return departure_time;
    }

    public LocalDateTime getArrival_time() {
        return arrival_time;
    }

    public String getStatus() {
        return status;
    }

    public Schedule(int scheduleId, int trainId, int routeId, LocalDateTime departure_time, LocalDateTime arrival_time, String status) {
        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.routeId = routeId;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.status = status;
    }

    public static Schedule parseResultSet(ResultSet rs) throws SQLException {
        return new Schedule(
            rs.getInt("schedule_id"),
            rs.getInt("train_id"),
            rs.getInt("route_id"),
            rs.getTimestamp("departure_time").toLocalDateTime(),
            rs.getTimestamp("arrival_time").toLocalDateTime(),
            rs.getString("status")
        );
    }
}
