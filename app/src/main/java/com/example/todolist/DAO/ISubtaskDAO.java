package com.example.todolist.DAO;

import com.example.todolist.model.Subtask;

import java.util.List;

public interface ISubtaskDAO {
    boolean addSubtask(Subtask subtask);
    boolean updateSubtask(Subtask subtask);
    boolean deleteSubtask(Subtask subtask);
    Subtask getSubtask(int id);
    List<Subtask> getAllSubtasks();
}
