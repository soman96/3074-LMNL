package com.example.lmnl.post;

import android.provider.BaseColumns;

public final class PostContract {

    private PostContract() {
        // Prevent instantiation
    }

    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    public static class LikeEntry implements BaseColumns {
        public static final String TABLE_NAME = "likes";
        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    public static class CommentEntry implements BaseColumns {
        public static final String TABLE_NAME = "comments";
        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}