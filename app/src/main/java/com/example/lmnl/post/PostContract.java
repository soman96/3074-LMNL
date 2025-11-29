package com.example.lmnl.post;

import android.provider.BaseColumns;

public final class PostContract {

    private PostContract() {
        // Prevent instantiation
    }

    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}