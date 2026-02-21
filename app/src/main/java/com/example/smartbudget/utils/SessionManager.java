package com.example.smartbudget.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session Manager to handle user login state
 */
public class SessionManager {
    private static final String PREF_NAME = "SmartBudgetSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(int userId, String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get logged in user ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get logged in user email
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get logged in user name
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "User");
    }

    /**
     * Logout user
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update user name
     */
    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }
}
