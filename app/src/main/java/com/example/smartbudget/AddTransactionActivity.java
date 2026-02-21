package com.example.smartbudget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.smartbudget.data.model.Transaction;
import com.example.smartbudget.databinding.ActivityAddTransactionBinding;
import com.example.smartbudget.repository.TransactionRepository;
import com.example.smartbudget.utils.SessionManager;

/**
 * Activity for adding new transactions
 */
public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private TransactionRepository transactionRepository;
    private SessionManager sessionManager;
    private boolean isExpense = true; // Default to expense
    
    // Expense categories (IDs 1-7)
    private String[] expenseCategories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Health", "Other"};
    private int[] expenseCategoryIds = {1, 2, 3, 4, 5, 6, 7};
    
    // Income categories (IDs 8-11)
    private String[] incomeCategories = {"Salary", "Friends and Family", "Business", "Other"};
    private int[] incomeCategoryIds = {8, 9, 10, 11};
    
    private int selectedCategoryId = 1;
    private ArrayAdapter<String> categoryAdapter;
    
    // Edit mode fields
    private boolean isEditMode = false;
    private int transactionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transactionRepository = new TransactionRepository(getApplication());
        sessionManager = new SessionManager(this);

        checkEditMode();
        setupCategorySpinner();
        setupTypeToggle();
        setupClickListeners();
    }
    
    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false);
        
        if (isEditMode) {
            // Update title
            binding.getRoot().post(() -> {
                // Find the title TextView and update it
                // For now, update the save button text
                binding.saveButton.setText("✓ UPDATE");
            });
            
            // Get transaction data
            transactionId = intent.getIntExtra("TRANSACTION_ID", -1);
            String name = intent.getStringExtra("TRANSACTION_NAME");
            double amount = intent.getDoubleExtra("TRANSACTION_AMOUNT", 0);
            String type = intent.getStringExtra("TRANSACTION_TYPE");
            int categoryId = intent.getIntExtra("TRANSACTION_CATEGORY_ID", 1);
            String notes = intent.getStringExtra("TRANSACTION_NOTES");
            
            // Pre-fill form
            binding.amountEditText.setText(String.valueOf((int) amount));
            
            if ("income".equals(type)) {
                isExpense = false;
                updateTypeButtons();
                updateCategorySpinner(); // Update categories for income
            }
            
            selectedCategoryId = categoryId;
            
            // Find the position of the category in the appropriate array
            int position = 0;
            if (isExpense) {
                for (int i = 0; i < expenseCategoryIds.length; i++) {
                    if (expenseCategoryIds[i] == categoryId) {
                        position = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < incomeCategoryIds.length; i++) {
                    if (incomeCategoryIds[i] == categoryId) {
                        position = i;
                        break;
                    }
                }
            }
            binding.categorySpinner.setSelection(position);
            
            if (notes != null && !notes.isEmpty()) {
                binding.notesEditText.setText(notes);
            }
        }
    }

    private void setupCategorySpinner() {
        // Initialize with expense categories
        updateCategorySpinner();

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the actual category ID based on type
                if (isExpense) {
                    selectedCategoryId = expenseCategoryIds[position];
                } else {
                    selectedCategoryId = incomeCategoryIds[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = isExpense ? 1 : 8; // Default to first category of each type
            }
        });
    }
    
    private void updateCategorySpinner() {
        // Get the appropriate categories based on transaction type
        String[] currentCategories = isExpense ? expenseCategories : incomeCategories;
        
        // Always create a new adapter to avoid issues
        categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currentCategories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
        
        // Reset selection to first category
        binding.categorySpinner.setSelection(0);
        selectedCategoryId = isExpense ? expenseCategoryIds[0] : incomeCategoryIds[0];
    }

    private void setupTypeToggle() {
        // Set initial state (Expense is selected)
        updateTypeButtons();

        binding.incomeButton.setOnClickListener(v -> {
            isExpense = false;
            updateTypeButtons();
            updateCategorySpinner(); // Update categories when switching to income
        });

        binding.expenseButton.setOnClickListener(v -> {
            isExpense = true;
            updateTypeButtons();
            updateCategorySpinner(); // Update categories when switching to expense
        });
    }

    private void updateTypeButtons() {
        if (isExpense) {
            // Expense button active
            binding.expenseButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.primary));
            binding.expenseButton.setTextColor(
                    ContextCompat.getColor(this, R.color.white));

            // Income button inactive
            binding.incomeButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.white));
            binding.incomeButton.setTextColor(
                    ContextCompat.getColor(this, android.R.color.darker_gray));
            binding.incomeButton.setStrokeColorResource(android.R.color.darker_gray);
        } else {
            // Income button active
            binding.incomeButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.primary));
            binding.incomeButton.setTextColor(
                    ContextCompat.getColor(this, R.color.white));

            // Expense button inactive
            binding.expenseButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.white));
            binding.expenseButton.setTextColor(
                    ContextCompat.getColor(this, android.R.color.darker_gray));
            binding.expenseButton.setStrokeColorResource(android.R.color.darker_gray);
        }
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.saveButton.setOnClickListener(v -> saveTransaction());

        binding.dashboardNavButton.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        binding.budgetNavButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MonthlyBudgetActivity.class));
            finish();
        });
    }

    private void saveTransaction() {
        String amountStr = binding.amountEditText.getText().toString().trim();
        String notes = binding.notesEditText.getText().toString().trim();

        // Validate amount
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            
            // Validate amount is positive
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Make amount negative for expenses
            if (isExpense) {
                amount = -Math.abs(amount);
            } else {
                amount = Math.abs(amount);
            }

            // Get category name based on the selected category ID
            String categoryName = "";
            if (isExpense) {
                for (int i = 0; i < expenseCategoryIds.length; i++) {
                    if (expenseCategoryIds[i] == selectedCategoryId) {
                        categoryName = expenseCategories[i];
                        break;
                    }
                }
            } else {
                for (int i = 0; i < incomeCategoryIds.length; i++) {
                    if (incomeCategoryIds[i] == selectedCategoryId) {
                        categoryName = incomeCategories[i];
                        break;
                    }
                }
            }
            
            // Get current user ID
            int userId = sessionManager.getUserId();
            
            // Validate user is logged in
            if (userId <= 0) {
                Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Create transaction name (category name or first part of notes)
            String transactionName = categoryName;
            String description = notes.isEmpty() ? null : notes;

            if (isEditMode && transactionId != -1) {
                // Update existing transaction
                Transaction transaction = new Transaction(
                        userId,
                        transactionName,
                        description,
                        amount,
                        System.currentTimeMillis(),
                        selectedCategoryId,
                        notes.isEmpty() ? null : notes
                );
                transaction.setId(transactionId);
                
                transactionRepository.updateTransaction(transaction);
                Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Create new transaction
                Transaction transaction = new Transaction(
                        userId,
                        transactionName,
                        description,
                        amount,
                        System.currentTimeMillis(),
                        selectedCategoryId,
                        notes.isEmpty() ? null : notes
                );

                transactionRepository.insertTransaction(transaction);
                Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
            }
            
            // Navigate back to dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
