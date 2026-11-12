package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user") // "user" is a reserved word in some databases
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // owning side: holds the FK column profile_id
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private UserProfile profile;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public void setProfile(UserProfile profile) { // keeps both sides in sync
        this.profile = profile;
        if (profile != null)
            profile.setUser(this);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserProfile getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username + "}";
    }
}
