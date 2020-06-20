package com.sayed.seu.forntend.utils;

public enum Term {
    MID("Mid"),
    FINAL("Final");

    private final String term;

    Term(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }
}
