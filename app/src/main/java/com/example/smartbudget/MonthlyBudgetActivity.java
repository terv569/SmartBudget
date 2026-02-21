package com.example.smartbudget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartbudget.databinding.ActivityMonthlyBudgetBinding;
import com.example.smartbudget.viewmodel.DashboardViewModel;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Activity for managing monthly budget
 */
public class MonthlyBudgetActivity extends AppCompatActivity {

    private ActivityMonthlyBudgetBinding binding;
    private DashboardViewModel viewModel;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "SmartBudgetPrefs";
    private static final String KEY_MONTHLY_BUDGET = "monthly_budget";
    private static final double DEFAULT_BUDGET = 20000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonthlyBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupClickListeners();
        loadBudgetData();
        observeExpenses();
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.dashboardNavButton.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        binding.addTransactionNavButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTransactionActivity.class));
            finish();
        });

        // Save budget button
        binding.saveBudgetButton.setOnClickListener(v -> saveBudget());
    }

    private void loadBudgetData() {
        // Load saved budget or use default
        double monthlyBudget = prefs.getFloat(KEY_MONTHLY_BUDGET, (float) DEFAULT_BUDGET);
        binding.budgetAmountEditText.setText(String.valueOf((int) monthlyBudget));
    }

    private void observeExpenses() {
        // Observe total expenses
        viewModel.getTotalExpenses().observe(this, expenses -> {
            if (expenses != null) {
                double totalExpenses = Math.abs(expenses); // Expenses are negative
                double monthlyBudget = getBudgetAmount();

                updateBudgetDisplay(monthlyBudget, totalExpenses);
            }
        });
    }

    private double getBudgetAmount() {
        String budgetStr = binding.budgetAmountEditText.getText().toString().trim();
        try {
            return Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            return DEFAULT_BUDGET;
        }
    }

    private void saveBudget() {
        String budgetStr = binding.budgetAmountEditText.getText().toString().trim();
        
        if (budgetStr.isEmpty()) {
            Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double budget = Double.parseDouble(budgetStr);
            prefs.edit().putFloat(KEY_MONTHLY_BUDGET, (float) budget).apply();
            Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show();
            
            // Refresh display
            viewModel.getTotalExpenses().observe(this, expenses -> {
                if (expenses != null) {
                    updateBudgetDisplay(budget, Math.abs(expenses));
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBudgetDisplay(double budget, double spent) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);

        // Calculate remaining
        double remaining = budget - spent;
        
        // Calculate percentage
        int percentage = (int) ((spent / budget) * 100);
        percentage = Math.min(percentage, 100); // Cap at 100%

        // Update UI
        binding.spentTextView.setText("KES " + formatter.format(spent));
        binding.remainingBudgetTextView.setText("KES " + formatter.format(Math.max(remaining, 0)));
        binding.percentageTextView.setText(percentage + "%");
        
        // Update circular progress
        binding.circularProgressBar.setProgress(percentage);
        
        // Show budget alert if exceeded
        checkBudgetAlert(budget, spent, percentage);
    }
    
    private void checkBudgetAlert(double budget, double spent, int percentage) {
        // Show toast alert if budget is exceeded
        if (spent > budget) {
            double overspent = spent - budget;
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMaximumFractionDigits(0);
            
            Toast.makeText(this, 
                "⚠️ Budget Exceeded!\nOverspent by KES " + formatter.format(overspent), 
                Toast.LENGTH_LONG).show();
        } else if (percentage >= 90) {
            // Warning when approaching limit
            Toast.makeText(this, 
                "⚠️ Warning: You've used " + percentage + "% of your budget", 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
