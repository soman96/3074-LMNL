package com.example.lmnl.post;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lmnl.R;
import com.example.lmnl.post.PostContract;
import com.example.lmnl.post.PostsDbHelper;

public class CreatePostActivity extends AppCompatActivity {

    private EditText etPostText;
    private Button btnSubmitPost;

    private PostsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post); // <-- your XML

        // Init views
        etPostText = findViewById(R.id.etPostText);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);

        // Init DB helper
        dbHelper = new PostsDbHelper(this);

        btnSubmitPost.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        String content = etPostText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PostContract.PostEntry.COLUMN_CONTENT, content);
        // created_at will be set automatically by DEFAULT CURRENT_TIMESTAMP

        long newRowId = db.insert(PostContract.PostEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error saving post", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Post saved", Toast.LENGTH_SHORT).show();
            etPostText.setText(""); // Clear input after save
            // You could finish() here if you want to go back to feed
            // finish();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
