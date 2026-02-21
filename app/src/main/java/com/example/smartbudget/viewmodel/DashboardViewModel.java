package com.example.smartbudget.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartbudget.data.model.Transaction;
import com.example.smartbudget.repository.TransactionRepository;
import com.example.smartbudget.utils.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel for Dashboard screen
 * Manages UI state and business logic for the dashboard
 */
public class DashboardViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final SessionManager sessionManager;

    // LiveData for transactions
    private final LiveData<List<Transaction>> recentTransactions;
    private final LiveData<Double> totalAmount;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpenses;

    // LiveData for formatted balance strings
    private final MutableLiveData<String> formattedBalance = new MutableLiveData<>();
    private final MutableLiveData<String> formattedIncome = new MutableLiveData<>();
    private final MutableLiveData<String> formattedExpenses = new MutableLiveData<>();

    private final NumberFormat currencyFormat;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
        sessionManager = new SessionManager(application);

        // Get current user ID
        int userId = sessionManager.getUserId();

        // Pass userId to all repository methods
        recentTransactions = transactionRepository.getRecentTransactions(userId, 10);
        totalAmount = transactionRepository.getTotalAmount(userId);
        totalIncome = transactionRepository.getTotalIncome(userId);
        totalExpenses = transactionRepository.getTotalExpenses(userId);

        // Use plain number format for KES currency
        currencyFormat = NumberFormat.getNumberInstance(Locale.US);
        currencyFormat.setMaximumFractionDigits(0);
        currencyFormat.setMinimumFractionDigits(0);
    }

    // Getters for LiveData
    public LiveData<List<Transaction>> getRecentTransactions() {
        return recentTransactions;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpenses() {
        return totalExpenses;
    }

    public LiveData<String> getFormattedBalance() {
        return formattedBalance;
    }

    public LiveData<String> getFormattedIncome() {
        return formattedIncome;
    }

    public LiveData<String> getFormattedExpenses() {
        return formattedExpenses;
    }

    // Format amount as KES currency string
    private String formatCurrency(double amount) {
        return "KES " + currencyFormat.format(amount);
    }

    // Update formatted values
    public void updateFormattedValues(double balance, double income, double expenses) {
        formattedBalance.setValue(formatCurrency(balance));
        formattedIncome.setValue(formatCurrency(income));
        formattedExpenses.setValue(formatCurrency(expenses));
    }
}