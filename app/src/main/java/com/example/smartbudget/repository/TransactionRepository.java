package com.example.smartbudget.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartbudget.data.AppDatabase;
import com.example.smartbudget.data.dao.TransactionDao;
import com.example.smartbudget.data.model.Transaction;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository class for managing Transaction data
 * Provides data access to ViewModels and UI layer
 */
public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
    }

    // User-specific transaction queries
    public LiveData<List<Transaction>> getAllTransactions(int userId) {
        return transactionDao.getAllTransactions(userId);
    }

    public LiveData<List<Transaction>> getRecentTransactions(int userId, int limit) {
        return transactionDao.getRecentTransactions(userId, limit);
    }

    public LiveData<Double> getTotalAmount(int userId) {
        return transactionDao.getTotalAmount(userId);
    }

    public LiveData<Double> getTotalIncome(int userId) {
        return transactionDao.getTotalIncome(userId);
    }

    public LiveData<Double> getTotalExpenses(int userId) {
        return transactionDao.getTotalExpenses(userId);
    }

    public LiveData<List<Transaction>> getTransactionsByCategory(int userId, int categoryId) {
        return transactionDao.getTransactionsByCategory(userId, categoryId);
    }

    public void insertTransaction(Transaction transaction) {
        executor.execute(() -> transactionDao.insertTransaction(transaction));
    }

    public void updateTransaction(Transaction transaction) {
        executor.execute(() -> transactionDao.updateTransaction(transaction));
    }

    public void deleteTransaction(Transaction transaction) {
        executor.execute(() -> transactionDao.deleteTransaction(transaction));
    }
}