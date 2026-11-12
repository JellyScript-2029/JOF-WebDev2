package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment", uniqueConstraints = @UniqueConstraint(name = "uk_student_course", columnNames = {
        "student_id", "course_id" }))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrollmentDate;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Enrollment() {
    }

    public Enrollment(Student student, Course course, LocalDate enrollmentDate, String grade) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Enrollment{id=" + id + "}";
    }
}
