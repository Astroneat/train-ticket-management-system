package com.mrt.admin.reports.models;

public class LinePoint {
    private String label;
    private int value;
    public LinePoint(String label, int value) {
        this.label = label;
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    public int getValue() {
        return value;
    }
}
