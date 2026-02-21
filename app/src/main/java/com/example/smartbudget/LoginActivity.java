package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartbudget.databinding.ActivityLoginBinding;
import com.example.smartbudget.viewmodel.AuthViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Login Activity - User authentication screen
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Set up observers
        setupObservers();

        // Set up click listeners
        setupClickListeners();
    }

    private void setupObservers() {
        // Observe authentication state
        authViewModel.getIsAuthenticated().observe(this, isAuthenticated -> {
            if (isAuthenticated) {
                navigateToDashboard();
            }
        });

        // Observe error messages
        authViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.loginButton.setEnabled(!isLoading);
            binding.loginButton.setText(isLoading ? "Loading..." : "Log In");
        });
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            // Perform login
            authViewModel.login(email, password);
        });

        binding.forgotPasswordTextView.setOnClickListener(v -> {
            // Handle forgot password
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.signUpTextView.setOnClickListener(v -> {
            // Navigate to Sign Up screen
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}