package com.mrt.services;

import java.util.Arrays;
import java.util.List;

import com.mrt.Universal;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Ticket;
import com.mrt.models.User;

public class TicketService {

    public enum ScanResult {
        NOT_FOUND,
        CANCELLED,
        EXPIRED,
        ALREADY_USED,
        WRONG_TIME,
        SUCCESS
    };

    private static final String CANCELLED = "cancelled";
    private static final String EXPIRED = "expired";
    private static final String BOARDED = "boarded";
    // private static final String BOOKED = "booked";

    public static void cancelTicket(int ticketId) { 
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'cancelled'
                WHERE ticket_id = ?;
            """,
            ticketId
        );
    }

    public static void boardTicket(int ticketId) {
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'boarded', scanned_at = UTC_TIMESTAMP()
                WHERE ticket_id = ?;     
            """,
            ticketId
        );
    }

    public static void expireTicket(int ticketId) {
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'expired'
                WHERE ticket_id = ?;        
            """,
            ticketId
        );
    }

    public static void refreshTicketsStatus() {
        Universal.db().execute(
            """
               UPDATE tickets tk
               INNER JOIN train_schedules ts ON tk.schedule_id = ts.schedule_id
               SET tk.status = 'cancelled'
               WHERE ts.status = 'cancelled' AND tk.status != 'cancelled';     
            """
        );

        Universal.db().execute(
            """
                UPDATE tickets tk
                INNER JOIN train_schedules ts ON ts.schedule_id = tk.schedule_id
                SET tk.status = 'expired'
                WHERE ts.status = 'completed' AND tk.status = 'booked';
            """
        );
    }

    public static ScanResult scan(Ticket ticket) {
        if(ticket == null) return ScanResult.NOT_FOUND;
        if(ticket.getStatus().equals(CANCELLED)) return ScanResult.CANCELLED;
        if(ticket.getStatus().equals(EXPIRED)) return ScanResult.EXPIRED;
        if(ticket.getStatus().equals(BOARDED)) return ScanResult.ALREADY_USED;
        if(ScheduleService.getScheduleById(ticket.getScheduleId()).isOngoing()) return ScanResult.WRONG_TIME;

        boardTicket(ticket.getTicketId());
        return ScanResult.SUCCESS;
    }

    public static List<Ticket> getTicketsBySchedule(Schedule schedule) {
        return Universal.db().query(
            """
                SELECT * FROM tickets
                WHERE schedule_id = ? AND status != 'cancelled';    
            """,
            rs -> Ticket.parseResultSet(rs),
            schedule.getScheduleId()
        );
    }

    public static List<Ticket> getTicketsByUser(User user, String searchTerm) {
        return Universal.db().query(
            """
                SELECT * FROM tickets
                INNER JOIN train_schedules ts ON tickets.schedule_id = ts.schedule_id
                INNER JOIN train_routes tr ON ts.route_id = tr.route_id
                INNER JOIN stations s1 ON tr.origin_station_id = s1.station_id
                INNER JOIN stations s2 ON tr.destination_station_id = s2.station_id
                WHERE tickets.user_id = ? AND (
                    tr.route_code LIKE ? OR
                    s1.station_name LIKE ? OR
                    s2.station_name LIKE ?
                )
                ORDER BY ts.departure_utc;
            """,
            rs -> Ticket.parseResultSet(rs),
            user.getUserId(),
            "%" + searchTerm + "%",
            "%" + searchTerm + "%",
            "%" + searchTerm + "%"
        );
    }

    public static Ticket getTicketById(int ticketId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM tickets
                WHERE ticket_id = ?        
            """,
            rs -> Ticket.parseResultSet(rs),
            ticketId
        );
    }

    public static Ticket getTicketByScheduleAndSeat(Schedule schedule, Seat seat) {
        return Universal.db().queryOne(
            """
                SELECT * FROM tickets tk
                WHERE tk.schedule_id = ? AND tk.car_no = ? AND tk.seat_index = ? AND status != 'cancelled'
            """,
            rs -> Ticket.parseResultSet(rs),
            schedule.getScheduleId(),
            seat.getCarNo(),
            seat.getSeatIndex()
        );
    }

    public static void bookTickets(User user, Schedule schedule, List<Seat> seats) {
        for(Seat seat: seats) {
            Universal.db().execute(
                """
                    INSERT INTO tickets (user_id, schedule_id, car_no, seat_index, status)
                    VALUES (?, ?, ?, ?, 'booked');    
                """,
                user.getUserId(),
                schedule.getScheduleId(),
                seat.getCarNo(),
                seat.getSeatIndex()
            );
        }
    }

    public static void bookTicket(User user, Schedule schedule, Seat seat) {
        bookTickets(user, schedule, Arrays.asList(new Seat[]{seat}));
    }
}
