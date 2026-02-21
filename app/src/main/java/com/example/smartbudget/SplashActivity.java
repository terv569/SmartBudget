package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartbudget.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        SessionManager sessionManager = new SessionManager(this);

        // Navigate after 2 seconds
        new Handler().postDelayed(() -> {
            // Check if user is already logged in AND has valid user ID
            if (sessionManager.isLoggedIn() && sessionManager.getUserId() > 0) {
                // User is logged in, go to Dashboard
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                // User is not logged in or has invalid session, go to Login
                sessionManager.logoutUser(); // Clear any invalid session
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}