package com.example.todolist.DAO;

import com.example.todolist.model.Note;

import java.util.List;

public interface INoteDAO {
    boolean addNote(Note note);
    boolean updateNote(Note note);
    boolean deleteNote(Note note);
    Note getNote(int id);
    List<Note> getAllNotes();
}
