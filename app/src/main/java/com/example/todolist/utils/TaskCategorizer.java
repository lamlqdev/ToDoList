package com.example.todolist.utils;

import com.example.todolist.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public static void sortTasks(List<Task> tasks, String sortByOptionSelected) {
        switch (sortByOptionSelected) {
            case "dueDate":
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        if (t1.getDueDate() == null && t2.getDueDate() == null) {
                            return compareDueTime(t1, t2);
                        } else if (t1.getDueDate() == null) {
                            return 1;
                        } else if (t2.getDueDate() == null) {
                            return -1;
                        }

                        int dateComparison = t1.getDueDate().compareTo(t2.getDueDate());
                        if (dateComparison != 0) {
                            return dateComparison;
                        }

                        return compareDueTime(t1, t2);
                    }

                    private int compareDueTime(Task t1, Task t2) {
                        if (t1.getDueTime() == null && t2.getDueTime() == null) {
                            return 0;
                        } else if (t1.getDueTime() == null) {
                            return 1;
                        } else if (t2.getDueTime() == null) {
                            return -1;
                        }
                        return t1.getDueTime().compareTo(t2.getDueTime());
                    }
                });
                break;

            case "createTime":
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        if (t1.getCreatedAt() == null && t2.getCreatedAt() == null) {
                            return 0;
                        } else if (t1.getCreatedAt() == null) {
                            return 1;
                        } else if (t2.getCreatedAt() == null) {
                            return -1;
                        }
                        return t1.getCreatedAt().compareTo(t2.getCreatedAt());
                    }
                });
                break;

            case "alphabetical":
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task t1, Task t2) {
                        if (t1.getTitle() == null && t2.getTitle() == null) {
                            return 0;
                        } else if (t1.getTitle() == null) {
                            return 1;
                        } else if (t2.getTitle() == null) {
                            return -1;
                        }
                        return t1.getTitle().compareToIgnoreCase(t2.getTitle());
                    }
                });
                break;

            default:
                throw new IllegalArgumentException("Invalid sort option: " + sortByOptionSelected);
        }
    }
}
