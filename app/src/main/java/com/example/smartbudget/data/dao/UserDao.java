package com.example.smartbudget.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartbudget.data.model.User;

import java.util.List;

/**
 * Data Access Object for User operations
 */
@Dao
public interface UserDao {
    @Insert
    long insertUser(User user); // Returns user ID

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserByIdSync(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmailSync(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User authenticateUser(String email, String password);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    @Query("UPDATE users SET balance = :balance WHERE id = :userId")
    void updateBalance(int userId, double balance);

    @Query("UPDATE users SET currency = :currency WHERE id = :userId")
    void updateCurrency(int userId, String currency);

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    LiveData<User> getLastCreatedUser();
}