package com.example.todolist.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.contract.TodolistContract;
import com.example.todolist.database.DBHandler;
import com.example.todolist.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskDAOImpl implements ITaskDAO{
    private DBHandler dbHandler;
    private Context context;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TaskDAOImpl(Context context) {
        this.dbHandler = new DBHandler(context);
        this.context = context;
    }
    @Override
    public boolean addTask(Task task) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.TasksEntry.TASK_ID, task.getTaskID());
            values.put(TodolistContract.TasksEntry.TITLE, task.getTitle());
            values.put(TodolistContract.TasksEntry.CATEGORY_ID, task.getCategoryID());
            if (task.getDueDate() != null) {
                values.put(TodolistContract.TasksEntry.DUE_DATE, task.getDueDate().format(dateFormatter));
            }
            if (task.getDueTime() != null) {
                values.put(TodolistContract.TasksEntry.DUE_TIME, task.getDueTime().format(timeFormatter));
            }
            values.put(TodolistContract.TasksEntry.STATUS, 1);
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.TasksEntry.CREATED_AT, now.format(dateTimeFormatter));

            long result = db.insert(TodolistContract.TasksEntry.TABLE_NAME, null, values);
            db.close();
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTask(Task task) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.TasksEntry.TITLE, task.getTitle());
            values.put(TodolistContract.TasksEntry.CATEGORY_ID, task.getCategoryID());
            if (task.getDueDate() != null) {
                values.put(TodolistContract.TasksEntry.DUE_DATE, task.getDueDate().format(dateFormatter));
            }
            if (task.getDueTime() != null) {
                values.put(TodolistContract.TasksEntry.DUE_TIME, task.getDueTime().format(timeFormatter));
            }

            int result = db.update(TodolistContract.TasksEntry.TABLE_NAME, values,
                    TodolistContract.TasksEntry.TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getTaskID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTaskStatus(Task task) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.TasksEntry.STATUS, task.getStatus());
            LocalDateTime now = LocalDateTime.now();
            values.put(TodolistContract.TasksEntry.UPDATED_AT, now.format(dateTimeFormatter));

            int result = db.update(TodolistContract.TasksEntry.TABLE_NAME, values,
                    TodolistContract.TasksEntry.TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getTaskID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTask(Task task) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int result = db.delete(TodolistContract.TasksEntry.TABLE_NAME,
                    TodolistContract.TasksEntry.TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getTaskID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean deleteAllCompletedTasks() {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int rowsAffected = db.delete(
                    TodolistContract.TasksEntry.TABLE_NAME,
                    TodolistContract.TasksEntry.STATUS + " = ?",
                    new String[]{ "2" }
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void deleteCompletedTasksByCategoryName(String categoryName) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        try {
            String query = "DELETE FROM tasks " +
                    "WHERE category_id = (SELECT category_id FROM categories WHERE name = ?) " +
                    "AND status = ?";

            db.execSQL(query, new String[]{categoryName, String.valueOf(2)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = null;
        Cursor cursor = null;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        try {
            String[] projection = {
                    TodolistContract.TasksEntry.TASK_ID,
                    TodolistContract.TasksEntry.TITLE,
                    TodolistContract.TasksEntry.CATEGORY_ID,
                    TodolistContract.TasksEntry.DUE_DATE,
                    TodolistContract.TasksEntry.DUE_TIME,
                    TodolistContract.TasksEntry.STATUS,
                    TodolistContract.TasksEntry.CREATED_AT,
                    TodolistContract.TasksEntry.UPDATED_AT
            };

            String selection = TodolistContract.TasksEntry.TASK_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query(TodolistContract.TasksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TASK_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TITLE));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CATEGORY_ID));
                String dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_DATE));
                String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_TIME));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.STATUS));
                String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CREATED_AT));
                String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.UPDATED_AT));

                LocalDate dueDate = dueDateString != null ? LocalDate.parse(dueDateString) : null;
                LocalTime dueTime = dueTimeString != null ? LocalTime.parse(dueTimeString) : null;
                LocalDateTime createdAt = LocalDateTime.parse(createdAtString, dateTimeFormatter);
                LocalDateTime updatedAt = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                task = new Task(taskId, title, categoryId, dueDate, dueTime, status, createdAt, updatedAt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {
                    TodolistContract.TasksEntry.TASK_ID,
                    TodolistContract.TasksEntry.TITLE,
                    TodolistContract.TasksEntry.CATEGORY_ID,
                    TodolistContract.TasksEntry.DUE_DATE,
                    TodolistContract.TasksEntry.DUE_TIME,
                    TodolistContract.TasksEntry.STATUS,
                    TodolistContract.TasksEntry.CREATED_AT,
                    TodolistContract.TasksEntry.UPDATED_AT
            };

            cursor = db.query(TodolistContract.TasksEntry.TABLE_NAME, projection, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TITLE));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CATEGORY_ID));
                    String dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_DATE));
                    String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_TIME));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.UPDATED_AT));

                    LocalDate dueDate = dueDateString != null ? LocalDate.parse(dueDateString) : null;
                    LocalTime dueTime = dueTimeString != null ? LocalTime.parse(dueTimeString) : null;
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtString, dateTimeFormatter);
                    LocalDateTime updatedAt = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Task task = new Task(taskId, title, categoryId, dueDate, dueTime, status, createdAt, updatedAt);
                    tasks.add(task);
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
        return tasks;
    }

    @Override
    public List<Task> getAllCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {
                    TodolistContract.TasksEntry.TASK_ID,
                    TodolistContract.TasksEntry.TITLE,
                    TodolistContract.TasksEntry.CATEGORY_ID,
                    TodolistContract.TasksEntry.DUE_DATE,
                    TodolistContract.TasksEntry.DUE_TIME,
                    TodolistContract.TasksEntry.STATUS,
                    TodolistContract.TasksEntry.CREATED_AT,
                    TodolistContract.TasksEntry.UPDATED_AT
            };

            String selection = TodolistContract.TasksEntry.STATUS + " = ?";
            String[] selectionArgs = { "2" };

            cursor = db.query(TodolistContract.TasksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TITLE));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CATEGORY_ID));
                    String dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_DATE));
                    String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_TIME));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.UPDATED_AT));

                    LocalDate dueDate = dueDateString != null ? LocalDate.parse(dueDateString) : null;
                    LocalTime dueTime = dueTimeString != null ? LocalTime.parse(dueTimeString) : null;
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtString, dateTimeFormatter);
                    LocalDateTime updatedAt = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Task task = new Task(taskId, title, categoryId, dueDate, dueTime, status, createdAt, updatedAt);
                    tasks.add(task);
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
        return tasks;
    }


    @Override
    public List<Task> getTasksByCategoryName(String categoryName) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT tasks.* " +
                    "FROM tasks " +
                    "INNER JOIN categories ON tasks.category_id = categories.category_id " +
                    "WHERE categories.name = ?";

            cursor = db.rawQuery(query, new String[]{categoryName});

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TITLE));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CATEGORY_ID));
                    String dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_DATE));
                    String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_TIME));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.UPDATED_AT));

                    LocalDate dueDate = dueDateString != null ? LocalDate.parse(dueDateString) : null;
                    LocalTime dueTime = dueTimeString != null ? LocalTime.parse(dueTimeString) : null;
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtString, dateTimeFormatter);
                    LocalDateTime updatedAt = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Task task = new Task(taskId, title, categoryId, dueDate, dueTime, status, createdAt, updatedAt);
                    tasks.add(task);
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
        return tasks;
    }

    @Override
    public List<Task> getCompletedTasksByCategoryName(String categoryName) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT tasks.* " +
                    "FROM tasks " +
                    "INNER JOIN categories ON tasks.category_id = categories.category_id " +
                    "WHERE categories.name = ? AND tasks.status = ?";

            cursor = db.rawQuery(query, new String[]{categoryName, String.valueOf(2)});

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TASK_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.TITLE));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CATEGORY_ID));
                    String dueDateString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_DATE));
                    String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.DUE_TIME));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.STATUS));
                    String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.CREATED_AT));
                    String updatedAtString = cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.TasksEntry.UPDATED_AT));

                    LocalDate dueDate = dueDateString != null ? LocalDate.parse(dueDateString) : null;
                    LocalTime dueTime = dueTimeString != null ? LocalTime.parse(dueTimeString) : null;
                    LocalDateTime createdAt = LocalDateTime.parse(createdAtString, dateTimeFormatter);
                    LocalDateTime updatedAt = updatedAtString != null ? LocalDateTime.parse(updatedAtString, dateTimeFormatter) : null;

                    Task task = new Task(taskId, title, categoryId, dueDate, dueTime, status, createdAt, updatedAt);
                    tasks.add(task);
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
        return tasks;
    }

    public boolean hasSubtasks(int taskId) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM subtasks WHERE task_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        return false;
    }

    public boolean hasNotes(int taskId) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM notes WHERE task_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        return false;
    }

}
