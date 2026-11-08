package com.example.tasktracker;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity tells JPA: "this class maps to a database table"
@Entity
// @Table sets the table name. Without it, the table would be named "user"
@Table(name = "app_users")
public class User {

    // @Id marks this field as the primary key
    @Id
    // IDENTITY lets MySQL auto-generate the id (AUTO_INCREMENT)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Long (wrapper class), not primitive long

    // nullable = false -> NOT NULL, unique = true -> no duplicates, length = 50 ->
    // VARCHAR(50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // NOT NULL, but no unique constraint
    @Column(nullable = false)
    private String email;

    // No annotation needed. JPA maps it to the column "created_at" automatically
    private LocalDateTime createdAt;

    // No-argument constructor: JPA REQUIRES this to create objects via reflection
    public User() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}