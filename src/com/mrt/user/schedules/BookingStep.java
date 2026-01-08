package com.mrt.user.schedules;

public enum BookingStep {
    SEARCH("Search"),
    SEATS("Seats"),
    SUMMARY("Summary"),
    SUCCESS("Success");

    private String label;
    BookingStep(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
