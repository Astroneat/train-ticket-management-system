package com.mrt.admin.reports.models;

public class BarData {
    private String label;
    private int value;
    public BarData(String label, int value) {
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
