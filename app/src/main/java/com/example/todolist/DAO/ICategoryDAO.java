package com.example.todolist.DAO;

import com.example.todolist.model.Category;

import java.util.List;

public interface ICategoryDAO {
    boolean addCategory(Category category);
    boolean updateCategory(Category category);
    boolean deleteCategory(Category category);
    Category getCategory(int id);
    List<Category> getAllCategories();
    int getIDByCategoryName (String categoryName);
}
