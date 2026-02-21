package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartbudget.databinding.ActivitySignupBinding;
import com.example.smartbudget.viewmodel.AuthViewModel;

/**
 * Signup Activity - User registration screen
 */
public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
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
                Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.signupButton.setEnabled(!isLoading);
            binding.signupButton.setText(isLoading ? "Creating Account..." : "Sign Up");
        });
    }

    private void setupClickListeners() {
        binding.signupButton.setOnClickListener(v -> {
            String name = binding.nameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform registration
            authViewModel.register(name, email, password);
        });

        binding.loginTextView.setOnClickListener(v -> {
            // Navigate back to login
            finish();
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
