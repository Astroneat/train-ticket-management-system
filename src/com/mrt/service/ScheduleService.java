package com.mrt.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.mrt.Universal;
import com.mrt.model.Schedule;

public class ScheduleService {

    public static final int SCHEDULE_VALID = 0;
    public static final int SCHEDULE_CONFLICT = 1;
    public static final int SCHEDULE_DEPARTURE_AFTER_ARRIVAL = 2;
    public static final int SCHEDULE_DEPARTURE_IN_THE_PAST = 3;

    public int createSchedule(int trainId, int routeId, LocalDateTime departure, LocalDateTime arrival) {
        Instant departureUTC = toUTC(departure);
        Instant arrivalUTC = toUTC(arrival);

        int state = validateSchedule(trainId, departureUTC, arrivalUTC);
        if(state != SCHEDULE_VALID) return state;

        Universal.db().execute(
            """
            INSERT INTO train_schedules(train_id, route_id, departure_utc, arrival_utc) VALUES (?, ?, ?, ?);   
            """,
            trainId,
            routeId,
            Timestamp.from(departureUTC),
            Timestamp.from(arrivalUTC)
        );
        return state;
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

    public List<Object[]> getSchedulesByTrain(int trainId, String status) {
        String sql = 
            """
            SELECT *
            FROM train_schedules ts
            INNER JOIN train_routes tr ON ts.route_id = tr.route_id AND ts.train_id = ?
            INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
            INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
            WHERE (ts.status = 'cancelled' OR ts.status = ?)
            """;

        if(status.equals("scheduled")) {
            sql += " AND Now() <= ts.departure_utc\n";
            sql += "ORDER BY ts.status ASC, ts.departure_utc ASC";
        } else if(status.equals("completed")) {
            sql += " AND ts.arrival_utc < NOW()";
            sql += "ORDER BY ts.status ASC, ts.departure_utc DESC";
        }
        sql += ";";

        return Universal.db().query(
            sql,
            rs -> new Object[] {
                Schedule.parseResultSet(rs),
                rs.getString("s1.station_name") + " → " + rs.getString("s2.station_name") + " (" + rs.getString("route_code") + ")",
            },
            trainId,
            status
        );
    }

    public List<Schedule> getSchedulesByRoute(int routeId, String status) {
        String sortOrder = status.equals("scheduled") ? "ASC" : "DESC";
        String sql = """
                SELECT * FROM train_schedules
                WHERE route_id = ? AND (status = ? OR status = 'cancelled')
                ORDER BY status DESC, departure_utc 
                """ + sortOrder + ";";

        return Universal.db().query(
            sql,
            rs -> Schedule.parseResultSet(rs),
            routeId,
            status
        );
    }

    private int validateSchedule(int trainId, Instant departure, Instant arrival) {
        if(departure.isAfter(arrival)) {
            System.out.println(departure + " " + arrival);
            // throw new IllegalArgumentException("Departure time must be before arrival time");
            return SCHEDULE_DEPARTURE_AFTER_ARRIVAL;
        }
        if(arrival.isBefore(Instant.now())) {
            // throw new IllegalArgumentException("Arrival time must occur in the future");
            return SCHEDULE_DEPARTURE_IN_THE_PAST;
        }

        String anotherSchedule = Universal.db().queryOne(
            """
            SELECT *
            FROM train_schedules ts
            INNER JOIN train_routes tr ON ts.route_id = tr.route_id AND ts.train_id = ?
            INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
            INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
            WHERE (? <= arrival_utc AND ? >= departure_utc); 
            """,
            rs -> new String(
                rs.getString("s1.station_name") + " → " + rs.getString("s2.station_name")
            ),
            trainId,
            Timestamp.from(departure),
            Timestamp.from(arrival)
        );
        if(anotherSchedule != null) {
            return SCHEDULE_CONFLICT;
            // JOptionPane.showMessageDialog(
            //     this, 
            //     "Another schedule already exists: " + anotherSchedule,
            //     "Schedule Error",
            //     JOptionPane.ERROR_MESSAGE
            // );
            // return;
        }
        return SCHEDULE_VALID;
    }

    private Instant toUTC(LocalDateTime dt) {
        return dt.atZone(ZoneId.systemDefault()).toInstant();
    }
}
