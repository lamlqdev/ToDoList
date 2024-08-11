package com.example.todolist.DAO;

import com.example.todolist.model.Task;

import java.time.LocalDate;
import java.util.List;

public interface ITaskDAO {
    boolean addTask(Task task);
    boolean updateTask(Task task);
    boolean updateTaskStatus(Task task);
    boolean deleteTask(Task task);
    boolean deleteAllCompletedTasks();
    void deleteCompletedTasksByCategoryName(String categoryName);
    Task getTask(int id);
    List<Task> getAllTasks();
    List<Task> getAllCompletedTasks();
    List<Task> getTasksByCategoryName(String categoryName);
    List<Task> getCompletedTasksByCategoryName(String categoryName);
    List<Task> searchTasksByName(String nameTask);
    void deleteTasksByCategoryName(String categoryName);
    List<Task> getTasksByDueDate(LocalDate dueDate);
    List<Task> getTasksForNext7Days();
    int getNumberCompletedTaskByDate(LocalDate date);
    int getNumberPendingTaskByCategory(int categoryID);
}
