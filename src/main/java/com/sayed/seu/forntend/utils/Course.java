package com.sayed.seu.forntend.utils;

public enum Course {
    INTERN("Intern"),
    RESEARCH("Research");

    private final String course;

    Course(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }
}
