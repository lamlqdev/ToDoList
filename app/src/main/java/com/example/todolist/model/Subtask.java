package com.example.todolist.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Subtask implements Serializable {
    private int subtaskID;
    private int taskID;
    private String description;
    private int status; // 1: Chưa hoàn thành, 2: Hoàn thành
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Subtask() {
    }

    public Subtask(int subtaskID, int taskID, String description, int status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.subtaskID = subtaskID;
        this.taskID = taskID;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getSubtaskID() {
        return subtaskID;
    }

    public void setSubtaskID(int subtaskID) {
        this.subtaskID = subtaskID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
