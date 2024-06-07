package com.example.todolist.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.contract.TodolistContract;
import com.example.todolist.database.DBHandler;
import com.example.todolist.model.Note;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NoteDAOImpl implements INoteDAO{
    DBHandler dbHandler;
    Context context;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public NoteDAOImpl(Context context){
        this.context = context;
        dbHandler = new DBHandler(context);
    }
    @Override
    public boolean addNote(Note note) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.NotesEntry.TASK_ID, note.getTaskID());
            values.put(TodolistContract.NotesEntry.CONTENT, note.getContent());
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.NotesEntry.CREATED_AT, now.format(formatter));

            long result = db.insert(TodolistContract.NotesEntry.TABLE_NAME, null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateNote(Note note) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.NotesEntry.CONTENT, note.getContent());
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.NotesEntry.UPDATED_AT, now.format(formatter));

            int result = db.update(TodolistContract.NotesEntry.TABLE_NAME, values,
                    TodolistContract.NotesEntry.NOTE_ID + " = ?",
                    new String[]{String.valueOf(note.getNoteID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteNote(Note note) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int result = db.delete(TodolistContract.NotesEntry.TABLE_NAME,
                    TodolistContract.NotesEntry.NOTE_ID + " = ?",
                    new String[]{String.valueOf(note.getNoteID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Note getNote(int id) {
        Note note = null;
        Cursor cursor = null;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        try {
            String[] projection = {
                    TodolistContract.NotesEntry.NOTE_ID,
                    TodolistContract.NotesEntry.TASK_ID,
                    TodolistContract.NotesEntry.CONTENT,
                    TodolistContract.NotesEntry.CREATED_AT,
                    TodolistContract.NotesEntry.UPDATED_AT
            };

            String selection = TodolistContract.NotesEntry.NOTE_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query(TodolistContract.NotesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.NOTE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.TASK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.CONTENT)),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.CREATED_AT))),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.UPDATED_AT)))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return note;
    }

    @Override
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] projection = {
                    TodolistContract.NotesEntry.NOTE_ID,
                    TodolistContract.NotesEntry.TASK_ID,
                    TodolistContract.NotesEntry.CONTENT,
                    TodolistContract.NotesEntry.CREATED_AT,
                    TodolistContract.NotesEntry.UPDATED_AT
            };

            cursor = db.query(TodolistContract.NotesEntry.TABLE_NAME, projection, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Note note = new Note(
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.NOTE_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.TASK_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.CONTENT)),
                            LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.CREATED_AT))),
                            LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.NotesEntry.UPDATED_AT)))
                    );
                    notes.add(note);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return notes;
    }
}
