package com.mrt.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.mrt.Universal;
import com.mrt.model.Schedule;

public class ScheduleService {
    
    public void createSchedule(int trainId, int routeId, LocalDateTime departure, LocalDateTime arrival) {
        validateSchedule(departure, arrival);

        Instant departureUTC = departure.atZone(ZoneId.systemDefault()).toInstant();
        Instant arrivalUTC = arrival.atZone(ZoneId.systemDefault()).toInstant();

        Universal.db().execute(
            """
            INSERT INTO train_schedules(train_id, route_id, departure_utc, arrival_utc, status) VALUES (?, ?, ?, ?, 'scheduled');   
            """,
            trainId,
            routeId,
            Timestamp.from(departureUTC),
            Timestamp.from(arrivalUTC)
        );
    }

    public void cancelSchedule(int scheduleId) {
        Universal.db().execute(
            """
            UPDATE train_schedules
            SET status = 'cancelled'
            WHERE schedule_id = ?;        
            """,
            scheduleId
        );
    }

    public List<Schedule> getSchedulesByTrain(int trainId, String status) {
        String sortOrder = status.equals("scheduled") ? "ASC" : "DESC";
        String sql = """
                SELECT * FROM train_schedules
                WHERE train_id = ? AND (status = ? OR status = 'cancelled')
                ORDER BY departure_utc 
                """ + sortOrder + ", status DESC;";

        return Universal.db().query(
            sql,
            rs -> Schedule.parseResultSet(rs),
            trainId,
            status
        );
    }

    public List<Schedule> getSchedulesByRoutSchedules(int routeId, String status) {
        String sortOrder = status.equals("scheduled") ? "ASC" : "DESC";
        String sql = """
                SELECT * FROM train_schedules
                WHERE route_id = ? AND (status = ? OR status = 'cancelled')
                ORDER BY departure_utc 
                """ + sortOrder + ", status DESC;";

        return Universal.db().query(
            sql,
            rs -> Schedule.parseResultSet(rs),
            routeId,
            status
        );
    }

    private void validateSchedule(LocalDateTime departure, LocalDateTime arrival) {
        if(departure.isAfter(arrival)) {
            throw new IllegalArgumentException("Departure time must be before arrival time");
        }
        if(arrival.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Arrival time must occur in the future");
        }
    }
}
