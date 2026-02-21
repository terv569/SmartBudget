package com.example.smartbudget.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartbudget.data.AppDatabase;
import com.example.smartbudget.data.dao.UserDao;
import com.example.smartbudget.data.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository for Authentication operations
 */
public class AuthRepository {
    private final UserDao userDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public AuthRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
    }

    /**
     * Register a new user
     */
    public void registerUser(String email, String name, String password, OnAuthCallback callback) {
        executor.execute(() -> {
            try {
                // Check if email already exists
                int emailExists = userDao.checkEmailExists(email);
                if (emailExists > 0) {
                    callback.onError("Email already registered");
                    return;
                }

                // Hash password
                String hashedPassword = hashPassword(password);

                // Create user with default values
                User user = new User(
                        email,
                        name,
                        hashedPassword,
                        0.0, // Initial balance
                        "KES", // Default currency
                        System.currentTimeMillis()
                );

                // Insert user and get ID
                long userId = userDao.insertUser(user);
                user.setId((int) userId);

                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError("Registration failed: " + e.getMessage());
            }
        });
    }

    /**
     * Login user
     */
    public void loginUser(String email, String password, OnAuthCallback callback) {
        executor.execute(() -> {
            try {
                // Hash password
                String hashedPassword = hashPassword(password);

                // Authenticate
                User user = userDao.authenticateUser(email, hashedPassword);

                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("Invalid email or password");
                }
            } catch (Exception e) {
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }

    /**
     * Get user by ID
     */
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    /**
     * Update user balance
     */
    public void updateUserBalance(int userId, double balance) {
        executor.execute(() -> userDao.updateBalance(userId, balance));
    }

    /**
     * Update user currency preference
     */
    public void updateUserCurrency(int userId, String currency) {
        executor.execute(() -> userDao.updateCurrency(userId, currency));
    }

    /**
     * Update user profile
     */
    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Callback interface for authentication operations
     */
    public interface OnAuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }
}
