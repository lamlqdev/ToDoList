package com.example.todolist.DAO;

import com.example.todolist.model.Category;
import com.example.todolist.model.Task;

import java.util.List;

public interface ICategoryDAO {
    boolean addCategory(Category category);
    boolean updateCategory(Category category);
    boolean deleteCategory(Category category);
    Category getCategory(int id);
    List<Category> getAllCategories();
    List<Category> getAllVisibleCategories();
    int getIDByCategoryName (String categoryName);
    List<Integer> getCategoryColorFromTasks(List<Task> tasks);
}
