package com.example.todolist.model;
import java.time.LocalDate;
import java.util.List;

public class CompletedTaskGroup {
    private LocalDate date;
    private List<Task> tasks;

    public CompletedTaskGroup(LocalDate date, List<Task> tasks) {
        this.date = date;
        this.tasks = tasks;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
