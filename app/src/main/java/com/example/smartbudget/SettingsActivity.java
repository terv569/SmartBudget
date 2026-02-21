package com.example.smartbudget;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartbudget.data.AppDatabase;
import com.example.smartbudget.data.dao.UserDao;
import com.example.smartbudget.data.model.User;
import com.example.smartbudget.databinding.ActivitySettingsBinding;
import com.example.smartbudget.repository.TransactionRepository;
import com.example.smartbudget.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Settings and Profile Activity
 * Manages app preferences, profile editing, and navigation
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences prefs;
    private SessionManager sessionManager;
    private static final String PREFS_NAME = "SmartBudgetPrefs";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_LANGUAGE = "language";
    
    private TransactionRepository transactionRepository;
    private UserDao userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sessionManager = new SessionManager(this);
        transactionRepository = new TransactionRepository(getApplication());
        userDao = AppDatabase.getInstance(getApplication()).userDao();

        loadUserProfile();
        loadPreferences();
        setupClickListeners();
        setupQuickNavigation();
    }

    private void loadUserProfile() {
        int userId = sessionManager.getUserId();
        
        // Load user data from database
        userDao.getUserById(userId).observe(this, user -> {
            if (user != null) {
                currentUser = user;
                displayUserProfile(user);
            }
        });
    }

    private void displayUserProfile(User user) {
        // Set name
        binding.profileNameTextView.setText(user.getName());
        
        // Set email
        binding.profileEmailTextView.setText(user.getEmail());
        
        // Set member since date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        String memberSince = "Member since " + dateFormat.format(new Date(user.getCreatedAt()));
        binding.memberSinceTextView.setText(memberSince);
    }

    private void loadPreferences() {
        // Load notification preference
        boolean notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
        binding.notificationsSwitch.setChecked(notificationsEnabled);
    }

    private void setupClickListeners() {
        // Profile Actions
        binding.editProfileLayout.setOnClickListener(v -> showEditProfileDialog());
        binding.emailSettingsLayout.setOnClickListener(v -> showEmailSettingsDialog());
        binding.phoneNumberLayout.setOnClickListener(v -> showPhoneNumberDialog());
        binding.languageLayout.setOnClickListener(v -> showLanguagePicker());
        
        // Sign Out Button
        binding.signOutButton.setOnClickListener(v -> showSignOutDialog());

        // Notifications Toggle
        binding.notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply();
            Toast.makeText(this, 
                isChecked ? "Notifications enabled" : "Notifications disabled", 
                Toast.LENGTH_SHORT).show();
        });
    }

    private void setupQuickNavigation() {
        // Dashboard Chip
        binding.chipDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Add Transaction Chip
        binding.chipAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // Budget Chip
        binding.chipBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, MonthlyBudgetActivity.class);
            startActivity(intent);
        });

        // Profile Chip - Already on this screen, just show toast
        binding.chipProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "Unable to load profile", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create dialog layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // Name input
        EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        nameInput.setText(currentUser.getName());
        nameInput.setHint("Full Name");
        layout.addView(nameInput);

        // Email input
        EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(currentUser.getEmail());
        emailInput.setHint("Email Address");
        emailInput.setPadding(0, 20, 0, 0);
        layout.addView(emailInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setMessage("Update your profile information")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newEmail = emailInput.getText().toString().trim();
                    
                    if (newName.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                        Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    updateUserProfile(newName, newEmail);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUserProfile(String newName, String newEmail) {
        new Thread(() -> {
            currentUser.setName(newName);
            currentUser.setEmail(newEmail);
            userDao.updateUser(currentUser);
            sessionManager.updateUserName(newName);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                binding.profileNameTextView.setText(newName);
                binding.profileEmailTextView.setText(newEmail);
            });
        }).start();
    }

    private void showEmailSettingsDialog() {
        String[] options = {"Change Email Address", "Email Notifications", "Privacy Settings"};
        
        new AlertDialog.Builder(this)
                .setTitle("Email Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditProfileDialog();
                            break;
                        case 1:
                            Toast.makeText(this, "Email notifications settings coming soon", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(this, "Privacy settings coming soon", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPhoneNumberDialog() {
        EditText phoneInput = new EditText(this);
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setHint("Enter phone number");
        phoneInput.setPadding(50, 30, 50, 30);

        new AlertDialog.Builder(this)
                .setTitle("Phone Number")
                .setMessage("Add or update your phone number")
                .setView(phoneInput)
                .setPositiveButton("Save", (dialog, which) -> {
                    String phoneNumber = phoneInput.getText().toString().trim();
                    if (phoneNumber.isEmpty()) {
                        Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Store phone number in preferences
                    prefs.edit().putString("phone_number", phoneNumber).apply();
                    Toast.makeText(this, "Phone number saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLanguagePicker() {
        String[] languages = {"English", "Swahili", "French"};
        String[] languageCodes = {"en", "sw", "fr"};
        
        String currentLanguage = prefs.getString(KEY_LANGUAGE, "en");
        int selectedIndex = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLanguage)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String selectedLanguage = languageCodes[which];
                    prefs.edit().putString(KEY_LANGUAGE, selectedLanguage).apply();
                    Toast.makeText(this, "Language updated to " + languages[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out", (dialog, which) -> performSignOut())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void performSignOut() {
        // Clear session
        sessionManager.logoutUser();
        
        // Clear preferences
        prefs.edit().clear().apply();
        
        // Navigate to Login activity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
