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
}
