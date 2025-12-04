package com.example.lmnl.user;

import android.provider.BaseColumns;

public final class UserContract {

    private UserContract() {
        // Prevent instantiation
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_FULL_NAME = "full_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_BIO = "bio";
        public static final String COLUMN_WEBSITE = "website";
    }
}
