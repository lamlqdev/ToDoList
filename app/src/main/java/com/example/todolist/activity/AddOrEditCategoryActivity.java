package com.example.todolist.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.databinding.ActivityAddOrEditCategoryBinding;

public class AddOrEditCategoryActivity extends AppCompatActivity {
    private ActivityAddOrEditCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}