package com.example.todolist.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.core.content.ContextCompat;

import com.example.todolist.R;
import com.example.todolist.contract.TodolistContract;
import com.example.todolist.database.DBHandler;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            int blueColor = ContextCompat.getColor(context, R.color.blue);
            values.put(TodolistContract.CategoryEntry.COLOR, blueColor);
            values.put(TodolistContract.CategoryEntry.IS_VISIBLE, 1);
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
            values.put(TodolistContract.CategoryEntry.COLOR, category.getColor());
            values.put(TodolistContract.CategoryEntry.IS_VISIBLE, category.isVisible() ? 1 : 0);

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
                    TodolistContract.CategoryEntry.NAME,
                    TodolistContract.CategoryEntry.COLOR,
                    TodolistContract.CategoryEntry.IS_VISIBLE
            };

            String selection = TodolistContract.CategoryEntry.CATEGORY_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            cursor = db.query(TodolistContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.COLOR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.IS_VISIBLE)) == 1
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
                    TodolistContract.CategoryEntry.NAME,
                    TodolistContract.CategoryEntry.COLOR,
                    TodolistContract.CategoryEntry.IS_VISIBLE
            };

            cursor = db.query(TodolistContract.CategoryEntry.TABLE_NAME, projection, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Category category = new Category(
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.NAME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.COLOR)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(TodolistContract.CategoryEntry.IS_VISIBLE)) == 1
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

    @Override
    public List<Integer> getCategoryColorFromTasks(List<Task> tasks) {
        Set<Integer> uniqueColors = new LinkedHashSet<>();

        for (Task task : tasks) {
            if (uniqueColors.size() > 3) break;
            if (task.getCategoryID() == -1){
                int blueColor = ContextCompat.getColor(context, R.color.blue);
                uniqueColors.add(blueColor);
            } else {
                Category category = getCategory(task.getCategoryID());
                uniqueColors.add(category.getColor());
            }

        }
        return new ArrayList<>(uniqueColors);
    }
}
