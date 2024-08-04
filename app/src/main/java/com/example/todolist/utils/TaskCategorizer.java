package com.example.todolist.utils;

import com.example.todolist.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskCategorizer {
    //categorize tasks into previous, today, and future and completed today
    public static void categorizeTasks(List<Task> tasks, List<Task> previousTasks, List<Task> todayTasks, List<Task> futureTasks, List<Task> completedTasks) {
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getStatus() == 2 && task.getUpdatedAt() != null) {
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

    //group tasks by completion date
    public static Map<LocalDate, List<Task>> groupTasksByCompletionDate(List<Task> tasks) {
        Map<LocalDate, List<Task>> groupedTasks = new HashMap<>();
        for (Task task : tasks) {
            LocalDate date = task.getUpdatedAt().toLocalDate();
            if (!groupedTasks.containsKey(date)) {
                groupedTasks.put(date, new ArrayList<>());
            }
            groupedTasks.get(date).add(task);
        }
        LocalDate today = LocalDate.now();
        Map<LocalDate, List<Task>> sortedGroupedTasks = new LinkedHashMap<>();
        groupedTasks.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    long diff1 = Math.abs(entry1.getKey().toEpochDay() - today.toEpochDay());
                    long diff2 = Math.abs(entry2.getKey().toEpochDay() - today.toEpochDay());
                    return Long.compare(diff1, diff2);
                })
                .forEachOrdered(entry -> sortedGroupedTasks.put(entry.getKey(), entry.getValue()));

        return sortedGroupedTasks;
    }
}
