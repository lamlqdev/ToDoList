package com.example.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.content.ContextCompat;

import com.example.todolist.R;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                "name TEXT UNIQUE, " +
                "color INTEGER" +
                ");";
        db.execSQL(CREATE_TABLE_CATEGORIES);

        String CREATE_TABLE_SUBTASKS = "CREATE TABLE subtasks (" +
                "subtask_id INTEGER PRIMARY KEY, " +
                "task_id INTEGER, " +
                "description TEXT, " +
                "status INTEGER, " +
                "created_at DATETIME, " +
                "updated_at DATETIME, " +
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
        int blueColor = ContextCompat.getColor(context, R.color.blue);
        String INSERT_DATE = "INSERT INTO categories (category_id, name, color) VALUES " +
                "(1, 'Work', " + blueColor + "), " +
                "(2, 'Home', " + blueColor + "), " +
                "(3, 'Wishlist', " + blueColor + "), " +
                "(4, 'Personal', " + blueColor + ")";
        db.execSQL(INSERT_DATE);
    }
}
