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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


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
            values.put(TodolistContract.SubtasksEntry.STATUS, 1);
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.SubtasksEntry.CREATED_AT, now.format(dateTimeFormatter));

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
            values.put(TodolistContract.SubtasksEntry.UPDATED_AT, now.format(dateTimeFormatter));

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
    public boolean deleteSubtaskByTaskID(int taskID) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int result = db.delete(TodolistContract.SubtasksEntry.TABLE_NAME,
                    TodolistContract.SubtasksEntry.TASK_ID + " = ?",
                    new String[]{String.valueOf(taskID)});
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
                int subtaskID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.SUBTASK_ID));
                int taskID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.TASK_ID));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.DESCRIPTION));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.STATUS));
                String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.CREATED_AT));
                String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.UPDATED_AT));

                LocalDateTime createdDateTime = createdAtString != null ? LocalDateTime.parse(createdAtString) : null;
                LocalDateTime updatedAtDateTime = updatedAtString != null ? LocalDateTime.parse(updatedAtString) : null;

                subtask = new Subtask(subtaskID, taskID, description, status, createdDateTime, updatedAtDateTime);
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
                    int subtaskID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.SUBTASK_ID));
                    int taskID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.TASK_ID));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.DESCRIPTION));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.UPDATED_AT));

                    LocalDateTime createdDateTime = createdAtString != null ? LocalDateTime.parse(createdAtString, dateTimeFormatter) : null;
                    LocalDateTime updatedAtDateTime = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Subtask subtask = new Subtask(subtaskID, taskID, description, status, createdDateTime, updatedAtDateTime);
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

    @Override
    public List<Subtask> getListSubtaskByTaskID(int taskID) {
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

            String selection = TodolistContract.SubtasksEntry.TASK_ID + " = ?";
            String[] selectionArgs = {String.valueOf(taskID)};

            cursor = db.query(TodolistContract.SubtasksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int subtaskID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.SUBTASK_ID));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.DESCRIPTION));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.SubtasksEntry.UPDATED_AT));

                    LocalDateTime createdDateTime = createdAtString != null ? LocalDateTime.parse(createdAtString, dateTimeFormatter) : null;
                    LocalDateTime updatedAtDateTime = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Subtask subtask = new Subtask(subtaskID, taskID, description, status, createdDateTime, updatedAtDateTime);
                    subtasks.add(subtask);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return subtasks;
    }
}
