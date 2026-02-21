package com.example.smartbudget.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * User model for authentication and profile
 */
@Entity(
    tableName = "users",
    indices = {@Index(value = "email", unique = true)}
)
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String email;
    private String name;
    private String password; // Hashed password
    private double balance; // Current account balance
    private String currency; // User's preferred currency (KES, USD, EUR)
    private long createdAt; // Creation timestamp as epoch milliseconds

    public User(String email, String name, String password, double balance, String currency, long createdAt) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}