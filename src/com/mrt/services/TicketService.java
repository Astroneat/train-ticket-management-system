package com.mrt.services;

import java.util.List;

import com.mrt.Universal;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Ticket;
import com.mrt.models.User;

public class TicketService {

    public static void cancelTicket(int ticketId) { 
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'cancelled'
                WHERE ticket_id = ?    
            """,
            ticketId
        );
    }

    public static void boardTicket(int ticketId) {
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'boarded'
                WHERE ticket_id = ?        
            """,
            ticketId
        );
    }

    public static void expireTicket(int ticketId) {
        Universal.db().execute(
            """
                UPDATE tickets SET status = 'expired'
                WHERE ticket_id = ?        
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

    public static List<Ticket> getTicketsBySchedule(Schedule schedule) {
        return Universal.db().query(
            """
                SELECT * FROM tickets
                WHERE schedule_id = ?;    
            """,
            rs -> Ticket.parseResultSet(rs),
            schedule.getScheduleId()
        );
    }

    public static List<Ticket> getTicketsByUser(User user) {
        return Universal.db().query(
            """
                SELECT * FROM tickets
                WHERE user_id = ?;    
            """,
            rs -> Ticket.parseResultSet(rs),
            user.getUserId()
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
}
