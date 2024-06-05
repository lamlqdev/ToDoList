package com.example.todolist.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Note implements Serializable {
    private int noteID;
    private int taskID;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {
    }

    public Note(int noteID, int taskID, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.noteID = noteID;
        this.taskID = taskID;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
