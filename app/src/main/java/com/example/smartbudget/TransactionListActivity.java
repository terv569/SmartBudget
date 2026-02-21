package com.example.smartbudget;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartbudget.adapter.TransactionAdapter;
import com.example.smartbudget.data.model.Transaction;
import com.example.smartbudget.databinding.ActivityTransactionListBinding;
import com.example.smartbudget.repository.TransactionRepository;
import com.example.smartbudget.utils.SessionManager;
import com.example.smartbudget.viewmodel.DashboardViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displaying full list of transactions with edit/delete capabilities
 */
public class TransactionListActivity extends AppCompatActivity {

    private ActivityTransactionListBinding binding;
    private DashboardViewModel viewModel;
    private TransactionAdapter transactionAdapter;
    private TransactionRepository transactionRepository;
    private SessionManager sessionManager;
    private List<Transaction> allTransactions = new ArrayList<>();
    private String currentFilter = "all";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        transactionRepository = new TransactionRepository(getApplication());
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        setupRecyclerView();
        setupClickListeners();
        setupFilterChips();
        observeTransactions();
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter();
        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionsRecyclerView.setAdapter(transactionAdapter);

        // Set item click listener for editing
        transactionAdapter.setOnItemClickListener(transaction -> {
            // Navigate to edit transaction
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("EDIT_MODE", true);
            intent.putExtra("TRANSACTION_ID", transaction.getId());
            intent.putExtra("TRANSACTION_NAME", transaction.getName());
            intent.putExtra("TRANSACTION_AMOUNT", Math.abs(transaction.getAmount()));
            intent.putExtra("TRANSACTION_TYPE", transaction.getAmount() < 0 ? "expense" : "income");
            intent.putExtra("TRANSACTION_CATEGORY_ID", transaction.getCategoryId());
            intent.putExtra("TRANSACTION_NOTES", transaction.getNotes());
            startActivity(intent);
        });

        // Setup swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<Transaction> currentList = transactionAdapter.getCurrentList();
                if (position >= 0 && position < currentList.size()) {
                    Transaction transaction = currentList.get(position);
                    showDeleteConfirmationDialog(transaction, position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.transactionsRecyclerView);
    }

    private void setupClickListeners() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.filterButton.setOnClickListener(v -> {
            // Future: Show date picker or advanced filters
            Toast.makeText(this, "Filter options coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFilterChips() {
        binding.chipAll.setOnClickListener(v -> filterTransactions("all"));
        binding.chipIncome.setOnClickListener(v -> filterTransactions("income"));
        binding.chipExpense.setOnClickListener(v -> filterTransactions("expense"));
    }

    private void observeTransactions() {
        viewModel.getRecentTransactions().removeObservers(this);
        
        // Get ALL transactions for current user
        transactionRepository.getAllTransactions(userId).observe(this, transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                filterTransactions(currentFilter);
                
                // Show/hide empty state
                if (transactions.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.transactionsRecyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    binding.transactionsRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void filterTransactions(String filter) {
        currentFilter = filter;
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            boolean matches = false;
            switch (filter) {
                case "all":
                    matches = true;
                    break;
                case "income":
                    matches = transaction.getAmount() >= 0;
                    break;
                case "expense":
                    matches = transaction.getAmount() < 0;
                    break;
            }
            if (matches) {
                filteredList.add(transaction);
            }
        }

        transactionAdapter.submitList(filteredList);
    }

    private void showDeleteConfirmationDialog(Transaction transaction, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?\n\n" +
                        transaction.getName() + "\nKES " + Math.abs(transaction.getAmount()))
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteTransaction(transaction);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Restore the item in the adapter
                    transactionAdapter.notifyItemChanged(position);
                })
                .setOnCancelListener(dialog -> {
                    // Restore the item if dialog is dismissed
                    transactionAdapter.notifyItemChanged(position);
                })
                .show();
    }

    private void deleteTransaction(Transaction transaction) {
        transactionRepository.deleteTransaction(transaction);
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh transactions when returning from edit
        observeTransactions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
