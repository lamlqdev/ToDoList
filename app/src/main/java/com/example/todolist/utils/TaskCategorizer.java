package com.example.todolist.utils;

import com.example.todolist.model.Task;

import java.time.LocalDate;
import java.util.List;

public class TaskCategorizer {
    public static void categorizeTasks(List<Task> tasks, List<Task> previousTasks, List<Task> todayTasks, List<Task> futureTasks, List<Task> completedTasks) {
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getStatus() == 2) {
                if (task.getUpdatedAt().toLocalDate().equals(today)){
                    completedTasks.add(task);
                }
            } else {
                if (task.getDueDate() != null) {
                    if (task.getDueDate().isBefore(today)) {
                        previousTasks.add(task);
                    } else if (task.getDueDate().isEqual(today)) {
                        todayTasks.add(task);
                    } else {
                        futureTasks.add(task);
                    }
                } else {
                    futureTasks.add(task);
                }
            }
        }
    }
}
