package com.example.todolist.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.MenuCategoryAdapter;
import com.example.todolist.databinding.ActivityUpdateTaskBinding;
import com.example.todolist.model.Category;

import java.util.List;

public class UpdateTaskActivity extends AppCompatActivity {
    private ActivityUpdateTaskBinding binding;
    private MenuCategoryAdapter menuCategoryAdapter;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Category> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTaskBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        categoryDAOImpl = new CategoryDAOImpl(this);
        categoryList = categoryDAOImpl.getAllCategories();
        menuCategoryAdapter = new MenuCategoryAdapter(this, R.layout.item_category_selected, categoryList);
        setWidgets();
        setEvents();
    }

    private void setEvents() {
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setWidgets() {
        binding.spinnerCategory.setAdapter(menuCategoryAdapter);
    }
}