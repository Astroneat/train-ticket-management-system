package com.mrt.user.tickets;

public enum TicketStatus {
    BOOKED("booked"),
    BOARDED("boarded"),
    CANCELLED("cancelled"),
    EXPIRED("expired");

    private final String status;
    TicketStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
