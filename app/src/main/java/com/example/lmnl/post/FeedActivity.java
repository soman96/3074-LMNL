package com.example.lmnl.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.lmnl.R;
import com.example.lmnl.post.Post;
import com.example.lmnl.post.PostContract;
import com.example.lmnl.post.PostsDbHelper;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostsAdapter postsAdapter;
    private PostsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rvPosts = findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter(new ArrayList<>());
        rvPosts.setAdapter(postsAdapter);

        dbHelper = new PostsDbHelper(this);

        // Load data the first time
        loadPostsFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload in case something changed (e.g., user created a new post)
        loadPostsFromDb();
    }

    private void loadPostsFromDb() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_CONTENT,
                PostContract.PostEntry.COLUMN_CREATED_AT
        };

        // ORDER BY created_at DESC (latest first)
        String sortOrder = PostContract.PostEntry.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                PostContract.PostEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        List<Post> posts = new ArrayList<>();

        try {
            int idColIndex = cursor.getColumnIndex(PostContract.PostEntry._ID);
            int contentColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CONTENT);
            int createdAtColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CREATED_AT);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColIndex);
                String content = cursor.getString(contentColIndex);
                String createdAt = cursor.getString(createdAtColIndex);

                posts.add(new Post(id, content, createdAt));
            }
        } finally {
            cursor.close();
        }

        postsAdapter.setPosts(posts);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}