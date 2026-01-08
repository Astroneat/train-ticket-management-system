package com.mrt.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Ticket {

    private int ticketId;
    private int userId;
    private int scheduleId;
    private int carNo;
    private int seatIndex;
    private String status;
    private LocalDateTime scannedAt;
    public int getTicketId() {
        return ticketId;
    }
    public int getUserId() {
        return userId;
    }
    public int getScheduleId() {
        return scheduleId;
    }
    public int getCarNo() {
        return carNo;
    }
    public int getSeatIndex() {
        return seatIndex;
    }
    public String getStatus() {
        return status;
    }
    public LocalDateTime getScannedAt() {
        return scannedAt;
    }
    public Ticket(int ticketId, int userId, int scheduleId, int carNo, int seatIndex, String status,
            LocalDateTime scannedAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.carNo = carNo;
        this.seatIndex = seatIndex;
        this.status = status;
        this.scannedAt = scannedAt;
    }

    public static Ticket parseResultSet(ResultSet rs) throws SQLException {
        Timestamp scannedAt = rs.getTimestamp("scanned_at");
        LocalDateTime ldt = null;
        if(scannedAt != null) ldt = scannedAt.toLocalDateTime();

        return new Ticket(
            rs.getInt("ticket_id"),
            rs.getInt("user_id"),
            rs.getInt("schedule_id"),
            rs.getInt("car_no"),
            rs.getInt("seat_index"),
            rs.getString("status"),
            ldt
        );
    }
}
