package com.example.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // after Task 2 step 2: link to the association entity, PERSIST/MERGE only
    @OneToMany(mappedBy = "student", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + "}";
    }
}
