package com.example.todolist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.CompletedTaskAdapter;
import com.example.todolist.databinding.ActivityTimeLineCompletedTaskBinding;
import com.example.todolist.model.CompletedTaskGroup;
import com.example.todolist.model.Task;
import com.example.todolist.utils.TaskCategorizer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimeLineCompletedTaskActivity extends AppCompatActivity {
    public static final int RESULT_OK = 1;
    private ActivityTimeLineCompletedTaskBinding binding;
    private TaskDAOImpl taskDAOImpl;
    private String currentCategory;
    private List<Task> completedTaskList;
    private CompletedTaskAdapter completedTaskAdapter;
    private List<CompletedTaskGroup> completedTaskGroups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeLineCompletedTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeData();
        setWidgets();
        setEvents();
    }

    private void setEvents() {
        binding.buttonBack.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        binding.deleteButton.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(TimeLineCompletedTaskActivity.this, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure want to delete ALL completed task?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentCategory.equals("All")) {
                                taskDAOImpl.deleteAllCompletedTasks();
                                completedTaskGroups.clear();
                                completedTaskAdapter.notifyDataSetChanged();
                            } else {
                                taskDAOImpl.deleteCompletedTasksByCategoryName(currentCategory);
                                completedTaskGroups.clear();
                                completedTaskAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        });
    }

    private void initializeData() {
        taskDAOImpl = new TaskDAOImpl(this);
        Intent intent = getIntent();
        currentCategory = intent.getStringExtra("Current Category");
        if (currentCategory.equals("All")){
            completedTaskList = taskDAOImpl.getAllCompletedTasks();
        } else {
            completedTaskList = taskDAOImpl.getCompletedTasksByCategoryName(currentCategory);
        }

        Map<LocalDate, List<Task>> groupedTasks = TaskCategorizer.groupTasksByCompletionDate(completedTaskList);

        completedTaskGroups = groupedTasks.entrySet().stream()
                .map(entry -> new CompletedTaskGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private void setWidgets() {
        completedTaskAdapter = new CompletedTaskAdapter(completedTaskGroups, this);
        binding.listCompletedTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.listCompletedTasks.setAdapter(completedTaskAdapter);
    }
}