package com.example.todolist.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int categoryID;
    private String name;
    private int color;
    private boolean isVisible;

    public Category(int categoryID, String name, int color, boolean isVisible) {
        this.categoryID = categoryID;
        this.name = name;
        this.color = color;
        this.isVisible = isVisible;
    }


    public Category() {
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
