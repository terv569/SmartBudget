package com.example.smartbudget; // Make sure this matches your package name

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartbudget.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    // Declare a variable for view binding
    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        // Set the content view to the root of the binding
        setContentView(binding.getRoot());

        // --- From here, you can start interacting with your UI elements ---

        // Example: Set a click listener for the "View All" button
        binding.viewAllButton.setOnClickListener(v -> {
            // Handle the button click, e.g., navigate to a full transaction list
        });

        // Example: Set a click listener for the "Add Transaction" button
        binding.addTransactionButton.setOnClickListener(v -> {
            // Handle the button click, e.g., open a new screen to add a transaction
        });
    }
}
