package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // shared entity: NO REMOVE cascade, NO orphanRemoval
    @OneToMany(mappedBy = "course", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Enrollment> enrollments = new ArrayList<>();

    public Course() {
    }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    @Override
    public String toString() {
        return "Course{id=" + id + ", title=" + title + "}";
    }
}
