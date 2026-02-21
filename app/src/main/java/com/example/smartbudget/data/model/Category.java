package com.example.smartbudget.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Category model for organizing transactions
 */
@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String icon; // Icon resource name (e.g., "ic_utensils")
    private String color; // Color hex code (e.g., "#4ADE80")

    public Category(String name, String icon, String color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}