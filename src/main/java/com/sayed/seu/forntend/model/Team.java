package com.sayed.seu.forntend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Team {
    private long id;
    private Faculty faculty;
    private Semester semester;
    private String courseName;
    private String term;
    private List<Student> students;
    private String topic;
}
