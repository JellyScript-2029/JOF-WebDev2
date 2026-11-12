package com.example.entity;

import jakarta.persistence.*;

@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    // inverse side: mappedBy is the Java FIELD name in User ("profile"), not a
    // column name
    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private User user;

    public UserProfile() {
    }

    public UserProfile(String bio) {
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserProfile{id=" + id + "}";
    }
}
