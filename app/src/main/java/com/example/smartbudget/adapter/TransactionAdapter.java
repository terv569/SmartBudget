package com.example.smartbudget.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartbudget.R;
import com.example.smartbudget.data.model.Transaction;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying transactions list
 */
public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId()
                    && oldItem.getName().equals(newItem.getName())
                    && oldItem.getAmount() == newItem.getAmount()
                    && oldItem.getDateEpoch() == newItem.getDateEpoch();
        }
    };

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = getItem(position);
        holder.bind(transaction, listener);
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout iconBackground;
        private final ImageView iconImageView;
        private final TextView nameTextView;
        private final TextView dateTextView;
        private final ImageView arrowIcon;
        private final TextView amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconBackground = itemView.findViewById(R.id.iconBackground);
            iconImageView = itemView.findViewById(R.id.transactionIcon);
            nameTextView = itemView.findViewById(R.id.transactionName);
            dateTextView = itemView.findViewById(R.id.transactionDate);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            amountTextView = itemView.findViewById(R.id.transactionAmount);
        }

        public void bind(Transaction transaction, OnItemClickListener listener) {
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(transaction);
                }
            });
            
            // Bind data
            // Set transaction name
            String transactionName = transaction.getName();
            nameTextView.setText(transactionName);
            
            // Set formatted date
            dateTextView.setText(formatDate(transaction.getDateEpoch()));

            // Set icon and background color based on transaction name/category
            String category = inferCategoryFromName(transactionName);
            int iconRes = getIconForCategory(category);
            int backgroundColor = getColorForCategory(category);
            
            iconImageView.setImageResource(iconRes);
            
            // Create circular background
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(backgroundColor);
            iconBackground.setBackground(drawable);

            // Format and display amount
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMaximumFractionDigits(0);
            String amountStr = formatter.format(Math.abs(transaction.getAmount()));
            amountTextView.setText(amountStr);

            // Set color based on transaction type
            int amountColor = ContextCompat.getColor(itemView.getContext(), 
                    transaction.getAmount() >= 0 ? R.color.success : R.color.error);
            amountTextView.setTextColor(amountColor);
            
            // Set arrow icon color
            int arrowTint = ContextCompat.getColor(itemView.getContext(),
                    transaction.getAmount() >= 0 ? R.color.success : R.color.error);
            arrowIcon.setColorFilter(arrowTint);
        }

        private String formatDate(long epochMillis) {
            Calendar today = Calendar.getInstance();
            Calendar transactionDate = Calendar.getInstance();
            transactionDate.setTimeInMillis(epochMillis);

            // Check if transaction is today
            if (today.get(Calendar.YEAR) == transactionDate.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == transactionDate.get(Calendar.DAY_OF_YEAR)) {
                return "Today";
            }
            
            // Check if transaction is yesterday
            today.add(Calendar.DAY_OF_YEAR, -1);
            if (today.get(Calendar.YEAR) == transactionDate.get(Calendar.YEAR)
                    && today.get(Calendar.DAY_OF_YEAR) == transactionDate.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday";
            }

            // Otherwise show date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.US);
            return dateFormat.format(new Date(epochMillis));
        }

        private String inferCategoryFromName(String name) {
            if (name == null) return "other";
            
            String lowerName = name.toLowerCase();
            
            // Food related keywords
            if (lowerName.contains("food") || lowerName.contains("restaurant") || 
                lowerName.contains("meal") || lowerName.contains("lunch") || 
                lowerName.contains("dinner") || lowerName.contains("breakfast") ||
                lowerName.contains("cafe") || lowerName.contains("grocery")) {
                return "food";
            }
            
            // Transport related keywords
            if (lowerName.contains("transport") || lowerName.contains("uber") || 
                lowerName.contains("taxi") || lowerName.contains("bus") || 
                lowerName.contains("fuel") || lowerName.contains("gas") ||
                lowerName.contains("parking")) {
                return "transport";
            }
            
            // Shopping related keywords
            if (lowerName.contains("shopping") || lowerName.contains("shop") ||
                lowerName.contains("store") || lowerName.contains("purchase")) {
                return "shopping";
            }
            
            // Entertainment related keywords
            if (lowerName.contains("entertainment") || lowerName.contains("movie") ||
                lowerName.contains("game") || lowerName.contains("concert")) {
                return "entertainment";
            }
            
            return "other";
        }

        private int getIconForCategory(String category) {
            if (category == null) return R.drawable.ic_wallet;
            
            switch (category.toLowerCase()) {
                case "food":
                    return R.drawable.ic_utensils;
                case "transport":
                    return R.drawable.ic_car;
                case "shopping":
                    return R.drawable.ic_wallet;
                default:
                    return R.drawable.ic_wallet;
            }
        }

        private int getColorForCategory(String category) {
            if (category == null) return 0xFFBB86FC;
            
            switch (category.toLowerCase()) {
                case "food":
                    return 0xFFF59E0B; // Orange
                case "transport":
                    return 0xFF3B82F6; // Blue
                case "shopping":
                    return 0xFFEC4899; // Pink
                case "entertainment":
                    return 0xFF8B5CF6; // Purple
                default:
                    return 0xFFBB86FC; // Default purple
            }
        }
    }
}