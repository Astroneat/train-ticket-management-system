package com.mrt.admin.reports.models;

public class TicketStatus {
    private String status;
    private int count;
    public TicketStatus(String status, int count) {
        this.status = status;
        this.count = count;
    }
    public String getStatus() {
        return status;
    }
    public int getCount() {
        return count;
    }
}
