package com.example.smartbudget.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartbudget.data.dao.CategoryDao;
import com.example.smartbudget.data.dao.TransactionDao;
import com.example.smartbudget.data.dao.UserDao;
import com.example.smartbudget.data.model.Category;
import com.example.smartbudget.data.model.Transaction;
import com.example.smartbudget.data.model.User;

/**
 * Main Room Database for SmartBudget
 */
@Database(
    entities = {Transaction.class, Category.class, User.class},
    version = 4,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract UserDao userDao();

    private static final RoomDatabase.Callback databaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Initialize default categories when database is first created
            initDefaultCategories(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Ensure all required categories exist (for existing databases)
            ensureAllCategories(db);
        }

        private void initDefaultCategories(SupportSQLiteDatabase db) {
            // Pre-populate with all expense and income categories
            // Expense categories (1-7)
            db.execSQL("INSERT INTO categories (name, icon, color) VALUES " +
                    "('Food', 'ic_utensils', '#F59E0B'), " +
                    "('Transport', 'ic_car', '#3B82F6'), " +
                    "('Shopping', 'ic_plus', '#EC4899'), " +
                    "('Entertainment', 'ic_trending_up', '#8B5CF6'), " +
                    "('Bills', 'ic_wallet', '#EF4444'), " +
                    "('Health', 'ic_trending_up', '#10B981'), " +
                    "('Other', 'ic_trending_up', '#6B7280')");
            
            // Income categories (8-11)
            db.execSQL("INSERT INTO categories (name, icon, color) VALUES " +
                    "('Salary', 'ic_wallet', '#10B981'), " +
                    "('Friends and Family', 'ic_trending_up', '#F59E0B'), " +
                    "('Business', 'ic_trending_up', '#3B82F6'), " +
                    "('Other', 'ic_trending_up', '#6B7280')");
        }

        private void ensureAllCategories(SupportSQLiteDatabase db) {
            // Insert missing categories if they don't exist (for database upgrades)
            // Using INSERT OR IGNORE to avoid duplicates
            
            // Expense categories (1-7)
            db.execSQL("INSERT OR IGNORE INTO categories (id, name, icon, color) VALUES " +
                    "(1, 'Food', 'ic_utensils', '#F59E0B'), " +
                    "(2, 'Transport', 'ic_car', '#3B82F6'), " +
                    "(3, 'Shopping', 'ic_plus', '#EC4899'), " +
                    "(4, 'Entertainment', 'ic_trending_up', '#8B5CF6'), " +
                    "(5, 'Bills', 'ic_wallet', '#EF4444'), " +
                    "(6, 'Health', 'ic_trending_up', '#10B981'), " +
                    "(7, 'Other', 'ic_trending_up', '#6B7280')");
            
            // Income categories (8-11)
            db.execSQL("INSERT OR IGNORE INTO categories (id, name, icon, color) VALUES " +
                    "(8, 'Salary', 'ic_wallet', '#10B981'), " +
                    "(9, 'Friends and Family', 'ic_trending_up', '#F59E0B'), " +
                    "(10, 'Business', 'ic_trending_up', '#3B82F6'), " +
                    "(11, 'Other', 'ic_trending_up', '#6B7280')");
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "smartbudget_database"
            )
            .fallbackToDestructiveMigration()
            .addCallback(databaseCallback)
            .build();
        }
        return instance;
    }
}