package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.adapter.CategoryManagerAdapter;
import com.example.todolist.databinding.ActivityAddOrEditCategoryBinding;
import com.example.todolist.model.Category;

import java.util.List;

public class AddOrEditCategoryActivity extends AppCompatActivity {
    private ActivityAddOrEditCategoryBinding binding;
    public static final int RESULT_OK = 1;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Category> categoryList;
    private CategoryManagerAdapter categoryManagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeData();
        setWidgets();
        setEvents();
    }

    private void initializeData() {
        categoryDAOImpl = new CategoryDAOImpl(this);
        categoryList = categoryDAOImpl.getAllCategories();
        categoryManagerAdapter = new CategoryManagerAdapter(categoryList, this);
    }

    private void setWidgets() {
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewCategories.setAdapter(categoryManagerAdapter);
    }

    private void setEvents() {
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        binding.buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}