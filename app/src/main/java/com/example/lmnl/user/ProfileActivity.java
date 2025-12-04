package com.example.lmnl.user;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmnl.MainActivity;
import com.example.lmnl.R;
import com.example.lmnl.auth.SessionManager;
import com.example.lmnl.post.Post;
import com.example.lmnl.post.PostContract;
import com.example.lmnl.post.PostsAdapter;
import com.example.lmnl.post.PostsDbHelper;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvDisplayName, tvUsername, tvWebsite, tvBio;
    private Button btnEditProfile, btnLogout;
    private RecyclerView rvUserPosts;
    private SessionManager sessionManager;
    private PostsDbHelper postsDbHelper;
    private PostsAdapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvUsername = findViewById(R.id.tvUsername);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvBio = findViewById(R.id.tvBio);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        rvUserPosts = findViewById(R.id.rvUserPosts);

        sessionManager = new SessionManager(this);
        postsDbHelper = new PostsDbHelper(this);

        // Setup RecyclerView
        rvUserPosts.setLayoutManager(new LinearLayoutManager(this));
        postsAdapter = new PostsAdapter(new ArrayList<>());
        rvUserPosts.setAdapter(postsAdapter);

        // Load user info
        loadUserInfo();

        // Load user's posts
        loadUserPosts();

        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user info in case it was updated
        loadUserInfo();
    }

    private void loadUserInfo() {
        String username = sessionManager.getUsername();
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        String bio = sessionManager.getBio();
        String website = sessionManager.getWebsite();

        tvDisplayName.setText(fullName != null ? fullName : "User");
        tvUsername.setText("@" + (username != null ? username : "username"));
        tvWebsite.setText(website != null && !website.isEmpty() ? website : (email != null ? email : ""));
        tvBio.setText(bio != null && !bio.isEmpty() ? bio : "Welcome to LMNL - Less Noise, More Connection");
    }

    private void loadUserPosts() {
        String currentUsername = sessionManager.getUsername();
        if (currentUsername == null) return;

        SQLiteDatabase db = postsDbHelper.getReadableDatabase();

        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_USERNAME,
                PostContract.PostEntry.COLUMN_CONTENT,
                PostContract.PostEntry.COLUMN_CREATED_AT
        };

        // Only get posts by current user
        String selection = PostContract.PostEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {currentUsername};
        String sortOrder = PostContract.PostEntry.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                PostContract.PostEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Post> posts = new ArrayList<>();

        try {
            int idColIndex = cursor.getColumnIndex(PostContract.PostEntry._ID);
            int usernameColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_USERNAME);
            int contentColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CONTENT);
            int createdAtColIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CREATED_AT);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColIndex);
                String username = cursor.getString(usernameColIndex);
                String content = cursor.getString(contentColIndex);
                String createdAt = cursor.getString(createdAtColIndex);

                posts.add(new Post(id, username, content, createdAt));
            }
        } finally {
            cursor.close();
        }

        postsAdapter.setPosts(posts);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        postsDbHelper.close();
        super.onDestroy();
    }
}