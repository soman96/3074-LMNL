package com.example.lmnl.post;

public class Post {
    private long id;
    private String content;
    private String createdAt;

    public Post(long id, String content, String createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}