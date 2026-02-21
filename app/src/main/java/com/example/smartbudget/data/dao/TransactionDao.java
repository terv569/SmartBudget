package com.example.smartbudget.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartbudget.data.model.Transaction;

import java.util.List;

/**
 * Data Access Object for Transaction operations
 */
@Dao
public interface TransactionDao {
    @Insert
    void insertTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    // User-specific queries
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY dateEpoch DESC")
    LiveData<List<Transaction>> getAllTransactions(int userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY dateEpoch DESC LIMIT :limit")
    LiveData<List<Transaction>> getRecentTransactions(int userId, int limit);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND dateEpoch >= :startDate AND dateEpoch <= :endDate ORDER BY dateEpoch DESC")
    LiveData<List<Transaction>> getTransactionsByDateRange(int userId, long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId")
    LiveData<Double> getTotalAmount(int userId);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amount > 0")
    LiveData<Double> getTotalIncome(int userId);

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND amount < 0")
    LiveData<Double> getTotalExpenses(int userId);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId ORDER BY dateEpoch DESC")
    LiveData<List<Transaction>> getTransactionsByCategory(int userId, int categoryId);

    @Query("DELETE FROM transactions WHERE userId = :userId")
    void deleteAllUserTransactions(int userId);
}