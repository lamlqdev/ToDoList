package com.example.todolist.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.contract.TodolistContract;
import com.example.todolist.database.DBHandler;
import com.example.todolist.model.Subtask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SubtaskDAOImpl implements ISubtaskDAO{
    DBHandler dbHandler;
    Context context;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SubtaskDAOImpl(Context context) {
        this.context = context;
        this.dbHandler = new DBHandler(context);
    }
    @Override
    public boolean addSubtask(Subtask subtask) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.SubtasksEntry.SUBTASK_ID, subtask.getSubtaskID());
            values.put(TodolistContract.SubtasksEntry.TASK_ID, subtask.getTaskID());
            values.put(TodolistContract.SubtasksEntry.DESCRIPTION, subtask.getDescription());
            values.put(TodolistContract.SubtasksEntry.STATUS, subtask.getStatus());
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.SubtasksEntry.CREATED_AT, now.format(formatter));

            long result = db.insert(TodolistContract.SubtasksEntry.TABLE_NAME, null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.SubtasksEntry.DESCRIPTION, subtask.getDescription());
            values.put(TodolistContract.SubtasksEntry.STATUS, subtask.getStatus());
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.SubtasksEntry.UPDATED_AT, now.format(formatter));

            int result = db.update(TodolistContract.SubtasksEntry.TABLE_NAME, values,
                    TodolistContract.SubtasksEntry.SUBTASK_ID + " = ?",
                    new String[]{String.valueOf(subtask.getSubtaskID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSubtask(Subtask subtask) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int result = db.delete(TodolistContract.SubtasksEntry.TABLE_NAME,
                    TodolistContract.SubtasksEntry.SUBTASK_ID + " = ?",
                    new String[]{String.valueOf(subtask.getSubtaskID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = null;
        Cursor cursor = null;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        try {
            String[] projection = {
                    TodolistContract.SubtasksEntry.SUBTASK_ID,
                    TodolistContract.SubtasksEntry.TASK_ID,
                    TodolistContract.SubtasksEntry.DESCRIPTION,
                    TodolistContract.SubtasksEntry.STATUS,
                    TodolistContract.SubtasksEntry.CREATED_AT,
                    TodolistContract.SubtasksEntry.UPDATED_AT
            };

            String selection = TodolistContract.SubtasksEntry.SUBTASK_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query(TodolistContract.SubtasksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                subtask = new Subtask(
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.SUBTASK_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.TASK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.STATUS)),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.CREATED_AT))),
                        LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.UPDATED_AT)))
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
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] projection = {
                    TodolistContract.SubtasksEntry.SUBTASK_ID,
                    TodolistContract.SubtasksEntry.TASK_ID,
                    TodolistContract.SubtasksEntry.DESCRIPTION,
                    TodolistContract.SubtasksEntry.STATUS,
                    TodolistContract.SubtasksEntry.CREATED_AT,
                    TodolistContract.SubtasksEntry.UPDATED_AT
            };

            cursor = db.query(TodolistContract.SubtasksEntry.TABLE_NAME, projection, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Subtask subtask = new Subtask(
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.SUBTASK_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.TASK_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.DESCRIPTION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.STATUS)),
                            LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.CREATED_AT))),
                            LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.UPDATED_AT)))
                    );
                    subtasks.add(subtask);
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
        return subtasks;
    }
}
