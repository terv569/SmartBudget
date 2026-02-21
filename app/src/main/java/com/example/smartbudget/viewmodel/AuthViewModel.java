package com.example.smartbudget.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.smartbudget.data.AppDatabase;
import com.example.smartbudget.data.dao.UserDao;
import com.example.smartbudget.data.model.User;
import com.example.smartbudget.utils.SessionManager;

/**
 * Handle user authentication and session management
 */
public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    private final UserDao userDao;
    private final SessionManager sessionManager;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        sessionManager = new SessionManager(application);
    }

    public MutableLiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Perform login with email and password
     */
    public void login(String email, String password) {
        // Basic validation
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            errorMessage.setValue("Please enter email and password");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address");
            return;
        }

        // Show loading
        isLoading.setValue(true);

        // Authenticate against database in background thread
        new Thread(() -> {
            try {
                User user = userDao.authenticateUser(email, password);
                
                // Update UI on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    isLoading.setValue(false);
                    
                    if (user != null) {
                        // Create session
                        sessionManager.createLoginSession(user.getId(), user.getEmail(), user.getName());
                        isAuthenticated.setValue(true);
                    } else {
                        errorMessage.setValue("Invalid email or password");
                    }
                });
            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Login error: " + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Create a new user account
     */
    public void register(String name, String email, String password) {
        // Basic validation
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || 
            password == null || password.isEmpty()) {
            errorMessage.setValue("All fields are required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Please enter a valid email address");
            return;
        }
        
        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return;
        }

        // Show loading
        isLoading.setValue(true);

        // Register user in background thread
        new Thread(() -> {
            try {
                // Check if email already exists
                int emailExists = userDao.checkEmailExists(email);
                
                if (emailExists > 0) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        isLoading.setValue(false);
                        errorMessage.setValue("Email already registered");
                    });
                    return;
                }
                
                // Create new user
                User newUser = new User(email, name, password, 0.0, "KES", System.currentTimeMillis());
                long userId = userDao.insertUser(newUser);
                
                // Update UI on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    isLoading.setValue(false);
                    
                    if (userId > 0) {
                        // Create session
                        sessionManager.createLoginSession((int) userId, email, name);
                        isAuthenticated.setValue(true);
                    } else {
                        errorMessage.setValue("Registration failed. Please try again.");
                    }
                });
            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Registration error: " + e.getMessage());
                });
            }
        }).start();
    }

    public void logout() {
        sessionManager.logoutUser();
        isAuthenticated.setValue(false);
    }
}