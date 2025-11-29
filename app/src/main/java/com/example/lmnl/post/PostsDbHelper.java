package com.example.lmnl.post;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PostsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "feed.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + PostContract.PostEntry.TABLE_NAME + " (" +
                    PostContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PostContract.PostEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                    PostContract.PostEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + PostContract.PostEntry.TABLE_NAME + ";";

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy: drop and recreate
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}