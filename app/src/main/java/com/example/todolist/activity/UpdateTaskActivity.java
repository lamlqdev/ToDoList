package com.example.todolist.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.SubtaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.SubtaskAdapter;
import com.example.todolist.databinding.ActivityUpdateTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Subtask;
import com.example.todolist.model.Task;

import java.util.List;

public class UpdateTaskActivity extends AppCompatActivity {
    private ActivityUpdateTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private SubtaskDAOImpl subtaskDAOImpl;
    private List<Category> categoryList;
    private List<Subtask> subtaskList;
    private SubtaskAdapter subtaskAdapter;
    private Task selectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTaskBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        selectedTask = (Task) getIntent().getSerializableExtra("task");

        initializeData();
        setWidgets();
        setEvents();
    }

    private void initializeData() {
        categoryDAOImpl = new CategoryDAOImpl(this);
        subtaskDAOImpl = new SubtaskDAOImpl(this);
        categoryList = categoryDAOImpl.getAllCategories();
        subtaskList = subtaskDAOImpl.getListSubtaskByTaskID(selectedTask.getTaskID());
    }

    private void setWidgets() {
        subtaskAdapter = new SubtaskAdapter(this, subtaskList);
        binding.subTaskList.setLayoutManager(new LinearLayoutManager(this));
        binding.subTaskList.setAdapter(subtaskAdapter);

        binding.titleTaskField.setText(selectedTask.getTitle());

        Category category = categoryDAOImpl.getCategory(selectedTask.getCategoryID());
        if (category != null) {
            binding.buttonAddCatagory.setText(category.getName());
            binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else {
            binding.buttonAddCatagory.setText(R.string.no_category);
            binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
        }

        if (selectedTask.getDueDate() != null) {
            binding.dueDateField.setText(selectedTask.getDueDate().toString());
        }

        if (selectedTask.getDueTime() != null) {
            binding.dueTimeField.setText(selectedTask.getDueTime().toString());
        }
    }

    private void setEvents() {
        binding.buttonAddCatagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryPopupMenu(view);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showCategoryPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "No Category");
        for(int i = 0; i < categoryList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i+1, Menu.NONE, categoryList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int selectedCategoryID = item.getItemId();
                binding.buttonAddCatagory.setText(item.getTitle());
                if (selectedCategoryID != 0) {
                    binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                } else {
                    binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_text));
                }
                return true;
            }
        });
        popupMenu.show();
    }

}