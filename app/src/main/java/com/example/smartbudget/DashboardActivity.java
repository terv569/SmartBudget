package com.example.smartbudget;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartbudget.adapter.TransactionAdapter;
import com.example.smartbudget.databinding.ActivityDashboardBinding;
import com.example.smartbudget.data.model.Transaction;
import com.example.smartbudget.viewmodel.DashboardViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

/**
 * Dashboard Activity - Main budget tracking screen
 */
public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private DashboardViewModel viewModel;
    private TransactionAdapter transactionAdapter;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "SmartBudgetPrefs";
    private static final String KEY_MONTHLY_BUDGET = "monthly_budget";
    private static final double DEFAULT_BUDGET = 20000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Set up RecyclerView
        setupRecyclerView();

        // Observe data
        setupObservers();

        // Set up click listeners
        setupClickListeners();
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter();
        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionsRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupObservers() {
        // Observe transactions
        viewModel.getRecentTransactions().observe(this, transactions -> {
            transactionAdapter.submitList(transactions);
        });

        // Observe balance
        viewModel.getTotalAmount().observe(this, balance -> {
            viewModel.updateFormattedValues(
                balance != null ? balance : 0,
                viewModel.getTotalIncome().getValue() != null ? viewModel.getTotalIncome().getValue() : 0,
                viewModel.getTotalExpenses().getValue() != null ? viewModel.getTotalExpenses().getValue() : 0
            );
        });

        // Observe total balance
        viewModel.getFormattedBalance().observe(this, balance -> {
            binding.balanceTextView.setText(balance);
        });
        
        // Observe expenses to calculate remaining budget
        viewModel.getTotalExpenses().observe(this, expenses -> {
            updateBudgetDisplay(expenses != null ? Math.abs(expenses) : 0);
        });
    }

    private void updateBudgetDisplay(double totalExpenses) {
        // Load saved budget
        double monthlyBudget = prefs.getFloat(KEY_MONTHLY_BUDGET, (float) DEFAULT_BUDGET);
        double remaining = monthlyBudget - totalExpenses;

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);

        // Update UI
        binding.monthlyBudgetTextView.setText("KES " + formatter.format(monthlyBudget));
        binding.remainingTextView.setText("KES " + formatter.format(Math.max(remaining, 0)));
    }

    private void setupClickListeners() {
        binding.settingsButton.setOnClickListener(v -> {
            // Navigate to settings
            startActivity(new android.content.Intent(this, SettingsActivity.class));
        });

        binding.viewAllButton.setOnClickListener(v -> {
            // Navigate to full transaction list
            startActivity(new android.content.Intent(this, TransactionListActivity.class));
        });

        binding.addTransactionButton.setOnClickListener(v -> {
            // Navigate to add transaction screen
            startActivity(new android.content.Intent(this, AddTransactionActivity.class));
        });
        
        // Navigate to Monthly Budget screen when clicking on budget cards
        binding.monthlyBudgetCard.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, MonthlyBudgetActivity.class));
        });
        
        binding.remainingCard.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, MonthlyBudgetActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh budget display when returning from MonthlyBudgetActivity
        viewModel.getTotalExpenses().observe(this, expenses -> {
            updateBudgetDisplay(expenses != null ? Math.abs(expenses) : 0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
