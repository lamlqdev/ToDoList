package com.example.todolist.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todolist.contract.TodolistContract;
import com.example.todolist.database.DBHandler;
import com.example.todolist.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements ICategoryDAO {
    DBHandler dbHandler;
    Context context;

    public CategoryDAOImpl(Context context) {
        this.dbHandler = new DBHandler(context);
        this.context = context;
    }
    @Override
    public boolean addCategory(Category category) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.CategoryEntry.NAME, category.getName());

            long result = db.insert(TodolistContract.CategoryEntry.TABLE_NAME, null, values);
            db.close();
            return result != -1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCategory(Category category) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TodolistContract.CategoryEntry.NAME, category.getName());

            int result = db.update(TodolistContract.CategoryEntry.TABLE_NAME, values,
                    TodolistContract.CategoryEntry.CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(category.getCategoryID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCategory(Category category) {
        try {
            SQLiteDatabase db = dbHandler.getWritableDatabase();
            int result = db.delete(TodolistContract.CategoryEntry.TABLE_NAME,
                    TodolistContract.CategoryEntry.CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(category.getCategoryID())});
            db.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Category getCategory(int id) {
        Category category = null;
        Cursor cursor = null;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        try {
            String[] projection = {
                    TodolistContract.CategoryEntry.CATEGORY_ID,
                    TodolistContract.CategoryEntry.NAME
            };

            String selection = TodolistContract.CategoryEntry.CATEGORY_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query(TodolistContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.NAME))
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
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] projection = {
                    TodolistContract.CategoryEntry.CATEGORY_ID,
                    TodolistContract.CategoryEntry.NAME
            };

            cursor = db.query(TodolistContract.CategoryEntry.TABLE_NAME, projection, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Category category = new Category(
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.NAME))
                    );
                    categories.add(category);
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
        return categories;
    }

    @Override
    public int getIDByCategoryName(String categoryName) {
        int categoryID = -1;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = null;
        try {
            String[] projection = {
                    TodolistContract.CategoryEntry.CATEGORY_ID
            };

            String selection = TodolistContract.CategoryEntry.NAME + " = ?";
            String[] selectionArgs = {categoryName};

            cursor = db.query(TodolistContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                categoryID = cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.CATEGORY_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return categoryID;
    }
}
