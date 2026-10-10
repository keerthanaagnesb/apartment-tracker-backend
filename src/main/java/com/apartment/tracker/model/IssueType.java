package com.apartment.tracker.model;

public enum IssueType {
    WATER("Water Issue"),
    LIFT("Lift Issue"),
    PARKING("Parking Issue"),
    SECURITY("Security Issue"),
    GARBAGE("Garbage Issue"),
    ELECTRICAL("Electrical Issue"),
    COMMON_AREA("Common Area Damage"),
    OTHER("Other");

    private final String label;

    IssueType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
