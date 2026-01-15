package com.mrt.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.mrt.Universal;
import com.mrt.models.Schedule;

public class ScheduleService {

    public static final int SCHEDULE_VALID = 0;
    public static final int SCHEDULE_CONFLICT = 1;
    public static final int SCHEDULE_DEPARTURE_AFTER_ARRIVAL = 2;
    public static final int SCHEDULE_DEPARTURE_IN_THE_PAST = 3;

    public static int createSchedule(int trainId, int routeId, LocalDateTime departure, LocalDateTime arrival) {
        refreshSchedulesStatus();

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

    public static void cancelSchedule(int scheduleId) {
        refreshSchedulesStatus();
        
        Universal.db().execute(
            """
            UPDATE train_schedules
            SET status = 'cancelled'
            WHERE schedule_id = ?;        
            """,
            scheduleId
        );
        TicketService.refreshTicketsStatus();
    }

    public static void refreshSchedulesStatus() {
        Universal.db().execute(
            """
                UPDATE train_schedules
                SET status = 'completed'
                WHERE status = 'scheduled' AND arrival_utc < UTC_TIMESTAMP()
            """
        );
    }

    public static int updateSchedule(int scheduleId, int trainId, LocalDateTime departure, LocalDateTime arrival) {
        Instant departureUTC = toUTC(departure);
        Instant arrivalUTC = toUTC(arrival);

        int state = validateSchedule(scheduleId, trainId, departureUTC, arrivalUTC);
        if(state != SCHEDULE_VALID) {
            return state;
        }

        Universal.db().execute(
            """
            UPDATE train_schedules
            SET departure_utc = ?, arrival_utc = ?
            WHERE schedule_id = ?
            """,
            Timestamp.from(departureUTC),
            Timestamp.from(arrivalUTC),
            scheduleId
        );
        return SCHEDULE_VALID;
    }   

    public static Schedule getScheduleById(int scheduleId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM train_schedules WHERE schedule_id = ?;
            """,
            rs -> Schedule.parseResultSet(rs),
            scheduleId
        );
    }

    public static List<Schedule> getSchedulesByTrain(int trainId, String status) {
        refreshSchedulesStatus();

        String sql = 
            """
            SELECT *
            FROM train_schedules ts
            WHERE ts.train_id = ? AND (ts.status = 'cancelled' OR ts.status = ?)
            """;

        if(status.equals("scheduled")) {
            // sql += " AND UTC_TIMESTAMP() <= ts.departure_utc\n";
            sql += "ORDER BY ts.status ASC, ts.departure_utc ASC";
        } else if(status.equals("completed")) {
            // sql += " AND ts.arrival_utc < UTC_TIMESTAMP()";
            sql += "ORDER BY ts.status ASC, ts.departure_utc DESC";
        }
        sql += ";";

        return Universal.db().query(
            sql,
            rs -> Schedule.parseResultSet(rs),
            trainId,
            status
        );
    }

    public static List<Schedule> getSchedulesByRoute(int routeId, String status) {
        refreshSchedulesStatus();

        String sortOrder = status.equals("scheduled") ? "ASC" : "DESC";
        String sql = """
                SELECT * FROM train_schedules
                WHERE route_id = ? AND (status = ? OR status = 'cancelled')
                ORDER BY status ASC, departure_utc 
                """ + sortOrder + ";";

        return Universal.db().query(
            sql,
            rs -> Schedule.parseResultSet(rs),
            routeId,
            status
        );
    }

    private static int validateSchedule(int excludeScheduleId, int trainId, Instant departure, Instant arrival) {
        if(departure.isAfter(arrival)) {
            return SCHEDULE_DEPARTURE_AFTER_ARRIVAL;
        }
        if(arrival.isBefore(Instant.now())) {
            return SCHEDULE_DEPARTURE_IN_THE_PAST;
        }
        if(hasOverlap(excludeScheduleId, trainId, departure, arrival)) {
            return SCHEDULE_CONFLICT;
        }

        return SCHEDULE_VALID;
    }

    private static int validateSchedule(int trainId, Instant departure, Instant arrival) {
        if(departure.isAfter(arrival)) {
            return SCHEDULE_DEPARTURE_AFTER_ARRIVAL;
        }
        if(arrival.isBefore(Instant.now())) {
            return SCHEDULE_DEPARTURE_IN_THE_PAST;
        }
        if(hasOverlap(-1, trainId, departure, arrival)) {
            return SCHEDULE_CONFLICT;
        }

        return SCHEDULE_VALID;
    }

    private static boolean hasOverlap(int excludeScheduleId, int trainId, Instant departure, Instant arrival) {
        String sql = 
        """
        SELECT *
        FROM train_schedules ts
        INNER JOIN train_routes tr ON ts.route_id = tr.route_id AND ts.train_id = ?
        INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
        INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
        WHERE (? <= arrival_utc AND ? >= departure_utc) 
        """;

        List<Object> args = new ArrayList<>();
        if(excludeScheduleId != -1) {
            sql += " AND ts.schedule_id != ?";
            args.add(excludeScheduleId);
        }
        sql += ";";

        args.add(trainId);
        args.add(Timestamp.from(departure));
        args.add(Timestamp.from(arrival));
        String anotherSchedule = Universal.db().queryOne(
            sql,
            rs -> new String(
                rs.getString("s1.station_name") + " → " + rs.getString("s2.station_name")
            ),
            args.toArray()
        );
        if(anotherSchedule != null) {
            return true;
        }
        return false;
    }

    private static Instant toUTC(LocalDateTime dt) {
        return dt.atZone(ZoneId.systemDefault()).toInstant();
    }
}
