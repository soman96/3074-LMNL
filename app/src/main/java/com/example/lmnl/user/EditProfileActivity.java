package com.example.lmnl.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lmnl.R;
import com.example.lmnl.auth.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etWebsite, etBio;
    private Button btnSaveProfile, btnCancel;
    private SessionManager sessionManager;
    private UserDbHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etWebsite = findViewById(R.id.etWebsite);
        etBio = findViewById(R.id.etBio);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancel);

        sessionManager = new SessionManager(this);
        userDbHelper = new UserDbHelper(this);

        // Load current profile data
        loadProfileData();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadProfileData() {
        String fullName = sessionManager.getFullName();
        String email = sessionManager.getEmail();
        String bio = sessionManager.getBio();
        String website = sessionManager.getWebsite();

        etFullName.setText(fullName != null ? fullName : "");
        etEmail.setText(email != null ? email : "");
        etBio.setText(bio != null ? bio : "");
        etWebsite.setText(website != null ? website : "");
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = sessionManager.getUsername();

        // Update in database
        SQLiteDatabase db = userDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_FULL_NAME, fullName);
        values.put(UserContract.UserEntry.COLUMN_EMAIL, email);
        values.put(UserContract.UserEntry.COLUMN_BIO, bio);
        values.put(UserContract.UserEntry.COLUMN_WEBSITE, website);

        String selection = UserContract.UserEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        // Update session
        sessionManager.updateProfile(fullName, email, bio, website);

        if (count > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        userDbHelper.close();
        super.onDestroy();
    }
}
