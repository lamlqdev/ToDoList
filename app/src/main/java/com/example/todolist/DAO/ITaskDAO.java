package com.example.todolist.DAO;

import com.example.todolist.model.Task;

import java.util.List;

public interface ITaskDAO {
    boolean addTask(Task task);
    boolean updateTask(Task task);
    boolean deleteTask(Task task);
    Task getTask(int id);
    List<Task> getAllTasks();
    List<Task> getTasksByCategoryName(String categoryName);
}
