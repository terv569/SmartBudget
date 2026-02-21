package com.example.smartbudget.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Transaction model representing income or expense
 */
@Entity(
    tableName = "transactions",
    foreignKeys = {
        @ForeignKey(
            entity = Category.class,
            parentColumns = "id",
            childColumns = "categoryId",
            onDelete = ForeignKey.RESTRICT
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("categoryId"), @Index("userId")}
)
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;           // Foreign key to User
    private String name;          // e.g., "Restaurant", "Salary"
    private String description;   // Optional description
    private double amount;       // Positive for income, negative for expense
    private long dateEpoch;      // Timestamp as epoch milliseconds
    private int categoryId;       // Foreign key to Category
    private String notes;         // Optional notes

    public Transaction(int userId, String name, String description, double amount, long dateEpoch, int categoryId, String notes) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.dateEpoch = dateEpoch;
        this.categoryId = categoryId;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDateEpoch() {
        return dateEpoch;
    }

    public void setDateEpoch(long dateEpoch) {
        this.dateEpoch = dateEpoch;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}