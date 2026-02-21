package com.example.smartbudget.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartbudget.data.AppDatabase;
import com.example.smartbudget.data.dao.CategoryDao;
import com.example.smartbudget.data.model.Category;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository class for managing Category data
 */
public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public CategoryRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        categoryDao = database.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(Category category) {
        executor.execute(() -> categoryDao.insertCategory(category));
    }

    public void updateCategory(Category category) {
        executor.execute(() -> categoryDao.updateCategory(category));
    }

    public void deleteCategory(Category category) {
        executor.execute(() -> categoryDao.deleteCategory(category));
    }
}