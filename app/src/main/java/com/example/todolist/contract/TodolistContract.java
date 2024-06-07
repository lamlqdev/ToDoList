package com.example.todolist.contract;

import android.provider.BaseColumns;

public final class TodolistContract {
    private TodolistContract(){}
    public static final class TasksEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String TASK_ID = "task_id";
        public static final String TITLE = "title";
        public static final String CATEGORY_ID = "category_id";
        public static final String DUE_DATE = "due_date";
        public static final String DUE_TIME = "due_time";
        public static final String STATUS = "status";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String CATEGORY_ID = "category_id";
        public static final String NAME = "name";
    }

    public static final class SubtasksEntry implements BaseColumns {
        public static final String TABLE_NAME = "subtasks";
        public static final String SUBTASK_ID = "subtask_id";
        public static final String TASK_ID = "task_id";
        public static final String DESCRIPTION = "description";
        public static final String STATUS = "status";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }

    public static final class NotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String NOTE_ID = "note_id";
        public static final String TASK_ID = "task_id";
        public static final String CONTENT = "content";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
    }
}
