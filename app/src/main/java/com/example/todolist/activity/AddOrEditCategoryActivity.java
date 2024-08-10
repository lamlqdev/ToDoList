package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.CategoryManagerAdapter;
import com.example.todolist.databinding.ActivityAddOrEditCategoryBinding;
import com.example.todolist.fragment.AddCategoryDialogFragment;
import com.example.todolist.model.Category;

import java.util.List;

public class AddOrEditCategoryActivity extends AppCompatActivity implements AddCategoryDialogFragment.OnCategoryAddedListener {
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
        setupBackPressedCallback();
    }

    private void initializeData() {
        categoryDAOImpl = new CategoryDAOImpl(this);
        categoryList = categoryDAOImpl.getAllCategories();
        categoryManagerAdapter = new CategoryManagerAdapter(categoryList, this, getSupportFragmentManager());
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
                AddCategoryDialogFragment addCategoryDialogFragment = AddCategoryDialogFragment.newInstance();;
                addCategoryDialogFragment.setOnCategoryAddedListener(AddOrEditCategoryActivity.this);
                addCategoryDialogFragment.show(getSupportFragmentManager(), "AddCategoryDialogFragment");
            }
        });
    }

    @Override
    public void onCategoryAdded(String categoryName) {
        if (!categoryName.isEmpty()) {
            Category newCategory = new Category();
            int blueColor = ContextCompat.getColor(this, R.color.blue);
            newCategory.setColor(blueColor);
            newCategory.setName(categoryName);
            newCategory.setVisible(true);
            if (categoryDAOImpl.addCategory(newCategory)){
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                categoryList.add(newCategory);
                categoryManagerAdapter.notifyItemInserted(categoryList.size() - 1);
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}