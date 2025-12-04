package com.example.lmnl.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LMNLSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BIO = "bio";
    private static final String KEY_WEBSITE = "website";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String username, String fullName, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void updateProfile(String fullName, String email, String bio, String website) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BIO, bio);
        editor.putString(KEY_WEBSITE, website);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getBio() {
        return prefs.getString(KEY_BIO, null);
    }

    public String getWebsite() {
        return prefs.getString(KEY_WEBSITE, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
