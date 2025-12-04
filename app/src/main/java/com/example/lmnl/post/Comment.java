package com.example.lmnl.post;

public class Comment {
    private long id;
    private long postId;
    private String username;
    private String content;
    private String createdAt;

    public Comment(long id, long postId, String username, String content, String createdAt) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
