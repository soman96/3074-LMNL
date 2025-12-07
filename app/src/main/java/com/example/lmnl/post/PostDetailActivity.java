package com.example.lmnl.post;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmnl.R;
import com.example.lmnl.auth.SessionManager;
import com.example.lmnl.user.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "post_id";

    private TextView tvAuthorName, tvTimestamp, tvPostContent, tvLikeCount, tvCommentCount, tvNoComments;
    private Button btnLike, btnComment, btnPostComment;
    private EditText etComment;
    private RecyclerView rvComments;
    private CommentsAdapter commentsAdapter;
    private PostsDbHelper dbHelper;
    private SessionManager sessionManager;
    private DailyLimitsManager limitsManager;
    private BottomNavigationView bottomNav;

    private long postId;
    private Post currentPost;
    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        postId = getIntent().getLongExtra(EXTRA_POST_ID, -1);
        if (postId == -1) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvAuthorName = findViewById(R.id.tvAuthorName);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvNoComments = findViewById(R.id.tvNoComments);
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnPostComment = findViewById(R.id.btnPostComment);
        etComment = findViewById(R.id.etComment);
        rvComments = findViewById(R.id.rvComments);

        dbHelper = new PostsDbHelper(this);
        sessionManager = new SessionManager(this);
        limitsManager = new DailyLimitsManager(this, sessionManager.getUsername());

        // Setup comments RecyclerView
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(new ArrayList<>());
        rvComments.setAdapter(commentsAdapter);

        // Load post and related data
        loadPost();
        checkIfLiked();
        loadComments();
        updateLikeCount();
        updateCommentCount();

        // Setup button listeners
        btnLike.setOnClickListener(v -> toggleLike());
        btnComment.setOnClickListener(v -> etComment.requestFocus());
        btnPostComment.setOnClickListener(v -> postComment());

        // Setup bottom navigation
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_back) {
                // Back button - finish this activity to return to feed
                finish();
                return true;
            } else if (itemId == R.id.menu_feed) {
                // Navigate to feed
                Intent intent = new Intent(PostDetailActivity.this, FeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_profile) {
                // Navigate to profile
                Intent intent = new Intent(PostDetailActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void loadPost() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PostContract.PostEntry._ID,
                PostContract.PostEntry.COLUMN_USERNAME,
                PostContract.PostEntry.COLUMN_CONTENT,
                PostContract.PostEntry.COLUMN_CREATED_AT
        };

        String selection = PostContract.PostEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(postId)};

        Cursor cursor = db.query(
                PostContract.PostEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int usernameIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_USERNAME);
            int contentIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CONTENT);
            int createdAtIndex = cursor.getColumnIndex(PostContract.PostEntry.COLUMN_CREATED_AT);

            String username = cursor.getString(usernameIndex);
            String content = cursor.getString(contentIndex);
            String createdAt = cursor.getString(createdAtIndex);

            currentPost = new Post(postId, username, content, createdAt);

            tvAuthorName.setText("@" + username);
            tvPostContent.setText(content);
            tvTimestamp.setText(formatTimestamp(createdAt));
        }

        cursor.close();
    }

    private void checkIfLiked() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String currentUsername = sessionManager.getUsername();

        String[] projection = {PostContract.LikeEntry._ID};
        String selection = PostContract.LikeEntry.COLUMN_POST_ID + " = ? AND " +
                PostContract.LikeEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {String.valueOf(postId), currentUsername};

        Cursor cursor = db.query(
                PostContract.LikeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        isLiked = cursor.getCount() > 0;
        cursor.close();

        updateLikeButton();
    }

    private void toggleLike() {
        String currentUsername = sessionManager.getUsername();

        if (isLiked) {
            // Unlike
            unlikePost(currentUsername);
        } else {
            // Like
            if (limitsManager.canLike()) {
                likePost(currentUsername);
                limitsManager.incrementLikesCount();
            } else {
                Toast.makeText(this, "Daily like limit reached!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void likePost(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PostContract.LikeEntry.COLUMN_POST_ID, postId);
        values.put(PostContract.LikeEntry.COLUMN_USERNAME, username);

        long result = db.insert(PostContract.LikeEntry.TABLE_NAME, null, values);

        if (result != -1) {
            isLiked = true;
            updateLikeButton();
            updateLikeCount();
            Toast.makeText(this, "Post liked!", Toast.LENGTH_SHORT).show();
        }
    }

    private void unlikePost(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = PostContract.LikeEntry.COLUMN_POST_ID + " = ? AND " +
                PostContract.LikeEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {String.valueOf(postId), username};

        int count = db.delete(PostContract.LikeEntry.TABLE_NAME, selection, selectionArgs);

        if (count > 0) {
            isLiked = false;
            updateLikeButton();
            updateLikeCount();
            Toast.makeText(this, "Post unliked!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLikeButton() {
        if (isLiked) {
            btnLike.setText("Unlike");
        } else {
            btnLike.setText("Like");
        }
    }

    private void updateLikeCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PostContract.LikeEntry._ID};
        String selection = PostContract.LikeEntry.COLUMN_POST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(postId)};

        Cursor cursor = db.query(
                PostContract.LikeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int likeCount = cursor.getCount();
        cursor.close();

        tvLikeCount.setText(likeCount + (likeCount == 1 ? " Like" : " Likes"));
    }

    private void updateCommentCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PostContract.CommentEntry._ID};
        String selection = PostContract.CommentEntry.COLUMN_POST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(postId)};

        Cursor cursor = db.query(
                PostContract.CommentEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int commentCount = cursor.getCount();
        cursor.close();

        tvCommentCount.setText(commentCount + (commentCount == 1 ? " Comment" : " Comments"));
    }

    private void loadComments() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PostContract.CommentEntry._ID,
                PostContract.CommentEntry.COLUMN_POST_ID,
                PostContract.CommentEntry.COLUMN_USERNAME,
                PostContract.CommentEntry.COLUMN_CONTENT,
                PostContract.CommentEntry.COLUMN_CREATED_AT
        };

        String selection = PostContract.CommentEntry.COLUMN_POST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(postId)};
        String sortOrder = PostContract.CommentEntry.COLUMN_CREATED_AT + " ASC";

        Cursor cursor = db.query(
                PostContract.CommentEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<Comment> comments = new ArrayList<>();

        try {
            int idIndex = cursor.getColumnIndex(PostContract.CommentEntry._ID);
            int postIdIndex = cursor.getColumnIndex(PostContract.CommentEntry.COLUMN_POST_ID);
            int usernameIndex = cursor.getColumnIndex(PostContract.CommentEntry.COLUMN_USERNAME);
            int contentIndex = cursor.getColumnIndex(PostContract.CommentEntry.COLUMN_CONTENT);
            int createdAtIndex = cursor.getColumnIndex(PostContract.CommentEntry.COLUMN_CREATED_AT);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                long commentPostId = cursor.getLong(postIdIndex);
                String username = cursor.getString(usernameIndex);
                String content = cursor.getString(contentIndex);
                String createdAt = cursor.getString(createdAtIndex);

                comments.add(new Comment(id, commentPostId, username, content, createdAt));
            }
        } finally {
            cursor.close();
        }

        commentsAdapter.setComments(comments);

        if (comments.isEmpty()) {
            tvNoComments.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.GONE);
        } else {
            tvNoComments.setVisibility(View.GONE);
            rvComments.setVisibility(View.VISIBLE);
        }
    }

    private void postComment() {
        String commentText = etComment.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!limitsManager.canComment()) {
            Toast.makeText(this, "Daily comment limit reached!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUsername = sessionManager.getUsername();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PostContract.CommentEntry.COLUMN_POST_ID, postId);
        values.put(PostContract.CommentEntry.COLUMN_USERNAME, currentUsername);
        values.put(PostContract.CommentEntry.COLUMN_CONTENT, commentText);

        long result = db.insert(PostContract.CommentEntry.TABLE_NAME, null, values);

        if (result != -1) {
            limitsManager.incrementCommentsCount();
            etComment.setText("");
            loadComments();
            updateCommentCount();
            Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            return timestamp;
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
