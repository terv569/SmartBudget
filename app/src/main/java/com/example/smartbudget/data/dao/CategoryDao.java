package com.example.smartbudget.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartbudget.data.model.Category;

import java.util.List;

/**
 * Data Access Object for Category operations
 */
@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<Category> getCategoryById(int categoryId);

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);
}