package com.example.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_TASKS = "CREATE TABLE tasks (" +
                "task_id INTEGER PRIMARY KEY, " +
                "title TEXT, " +
                "category_id INTEGER, " +
                "due_date DATETIME, " +
                "due_time TIME, " +
                "status INTEGER, " +
                "created_at DATETIME, " +
                "updated_at DATETIME " +
                ");";
        db.execSQL(CREATE_TABLE_TASKS);

        String CREATE_TABLE_CATEGORIES = "CREATE TABLE categories (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE" +
                ");";
        db.execSQL(CREATE_TABLE_CATEGORIES);

        String CREATE_TABLE_SUBTASKS = "CREATE TABLE subtasks (" +
                "subtask_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER, " +
                "description TEXT, " +
                "status INTEGER, " +
                "created_at DATETIME, " +
                "FOREIGN KEY (task_id) REFERENCES tasks(task_id)" +
                ");";
        db.execSQL(CREATE_TABLE_SUBTASKS);

        String CREATE_TABLE_NOTES = "CREATE TABLE notes (" +
                "note_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER, " +
                "content TEXT, " +
                "created_at DATETIME, " +
                "updated_at DATETIME, " +
                "FOREIGN KEY (task_id) REFERENCES tasks(task_id)" +
                ");";
        db.execSQL(CREATE_TABLE_NOTES);
        insertCategory(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS subtasks");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    private void insertCategory(SQLiteDatabase db) {
        String INSERT_DATE = "INSERT INTO categories (category_id, name)"
                + "VALUES (1, 'Work'), (2, 'Home'), (3, 'Personal')";
        db.execSQL(INSERT_DATE);
    }
}
