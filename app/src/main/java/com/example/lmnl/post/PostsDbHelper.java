package com.example.lmnl.post;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PostsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "feed.db";
    private static final int DATABASE_VERSION = 3; // Incremented for likes and comments tables

    private static final String SQL_CREATE_POSTS_TABLE =
            "CREATE TABLE " + PostContract.PostEntry.TABLE_NAME + " (" +
                    PostContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PostContract.PostEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                    PostContract.PostEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                    PostContract.PostEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    private static final String SQL_CREATE_LIKES_TABLE =
            "CREATE TABLE " + PostContract.LikeEntry.TABLE_NAME + " (" +
                    PostContract.LikeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PostContract.LikeEntry.COLUMN_POST_ID + " INTEGER NOT NULL, " +
                    PostContract.LikeEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                    PostContract.LikeEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(" + PostContract.LikeEntry.COLUMN_POST_ID + ", " +
                    PostContract.LikeEntry.COLUMN_USERNAME + ")" +
                    ");";

    private static final String SQL_CREATE_COMMENTS_TABLE =
            "CREATE TABLE " + PostContract.CommentEntry.TABLE_NAME + " (" +
                    PostContract.CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PostContract.CommentEntry.COLUMN_POST_ID + " INTEGER NOT NULL, " +
                    PostContract.CommentEntry.COLUMN_USERNAME + " TEXT NOT NULL, " +
                    PostContract.CommentEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                    PostContract.CommentEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    private static final String SQL_DROP_POSTS_TABLE =
            "DROP TABLE IF EXISTS " + PostContract.PostEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_LIKES_TABLE =
            "DROP TABLE IF EXISTS " + PostContract.LikeEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_COMMENTS_TABLE =
            "DROP TABLE IF EXISTS " + PostContract.CommentEntry.TABLE_NAME + ";";

    private Context context;

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POSTS_TABLE);
        db.execSQL(SQL_CREATE_LIKES_TABLE);
        db.execSQL(SQL_CREATE_COMMENTS_TABLE);
        // Add mock data on first creation
        addMockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy: drop and recreate
        db.execSQL(SQL_DROP_COMMENTS_TABLE);
        db.execSQL(SQL_DROP_LIKES_TABLE);
        db.execSQL(SQL_DROP_POSTS_TABLE);
        onCreate(db);
    }

    private void addMockData(SQLiteDatabase db) {
        // Add some fake posts from other users
        String[] mockUsernames = {"alex_chen", "sarah_j", "mike_ross", "emma_w", "david_kim"};
        String[] mockPosts = {
                "Just finished reading a great book on mindfulness. Highly recommend!",
                "Beautiful sunset today. Taking time to appreciate the small things.",
                "New project at work is challenging but exciting. Learning so much!",
                "Had an amazing coffee at the new cafe downtown. Must try their latte!",
                "Finally completed my morning workout routine. Feeling energized!",
                "Trying out a new recipe tonight. Wish me luck!",
                "The weather is perfect for a long walk in the park.",
                "Just discovered a hidden gem of a restaurant. The food is incredible!",
                "Working on improving my productivity. Small steps every day.",
                "Grateful for good friends and meaningful conversations."
        };

        for (int i = 0; i < mockPosts.length; i++) {
            ContentValues values = new ContentValues();
            values.put(PostContract.PostEntry.COLUMN_USERNAME, mockUsernames[i % mockUsernames.length]);
            values.put(PostContract.PostEntry.COLUMN_CONTENT, mockPosts[i]);
            // Let created_at use DEFAULT CURRENT_TIMESTAMP
            db.insert(PostContract.PostEntry.TABLE_NAME, null, values);
        }
    }
}