package com.sayed.seu.forntend.utils;

public enum Semester {
    SPRING("Spring"),
    FALL("Fall"),
    SUMMER("Summer");

    private final String semester;

    Semester(String semester) {
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }
}
