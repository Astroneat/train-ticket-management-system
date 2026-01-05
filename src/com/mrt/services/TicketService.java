package com.mrt.services;

import com.mrt.Universal;

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
                INNER JOIN train_schedules ts ON tk.schedule_id = tk.schedule_id
                SET tk.status = 'expired'
                WHERE ts.arrival_utc < UTC_TIMESTAMP()
                AND tk.status= 'booked';
            """
        );
    }
}
