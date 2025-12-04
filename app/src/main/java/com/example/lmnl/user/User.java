package com.example.lmnl.user;

public class User {
    private long id;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String bio;
    private String website;

    public User(long id, String username, String fullName, String email, String password, String bio, String website) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.website = website;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getWebsite() {
        return website;
    }
}
