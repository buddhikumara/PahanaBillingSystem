package com.pahana.persistence.model;

import java.time.LocalDateTime; // Java 8+

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt; // read-only in UI

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
