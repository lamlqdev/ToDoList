package com.example.todolist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.NoteDAOImpl;
import com.example.todolist.DAO.SubtaskDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.SubtaskAdapter;
import com.example.todolist.databinding.ActivityUpdateTaskBinding;
import com.example.todolist.fragment.DateDialogFragment;
import com.example.todolist.model.Category;
import com.example.todolist.model.Note;
import com.example.todolist.model.Subtask;
import com.example.todolist.model.Task;
import com.example.todolist.utils.IDGenerator;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class UpdateTaskActivity extends AppCompatActivity implements DateDialogFragment.OnDateSelectedListener {
    public static final int RESULT_OK = 1;
    private ActivityUpdateTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private SubtaskDAOImpl subtaskDAOImpl;
    private TaskDAOImpl taskDAOImpl;
    private NoteDAOImpl noteDAOImpl;
    private List<Category> categoryList;
    private List<Subtask> subtaskList;
    private SubtaskAdapter subtaskAdapter;
    private Task selectedTask;
    private ActivityResultLauncher<Intent> addNotesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTaskBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            selectedTask = (Task) intent.getSerializableExtra("task");
        }

        addNotesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AddOrEditNoteActivity.RESULT_OK) {
                        binding.noteField.setText(R.string.edit);
                    }
                }
        );

        initializeData();
        setWidgets();
        setEvents();
    }

    private void initializeData() {
        categoryDAOImpl = new CategoryDAOImpl(this);
        subtaskDAOImpl = new SubtaskDAOImpl(this);
        taskDAOImpl = new TaskDAOImpl(this);
        noteDAOImpl = new NoteDAOImpl(this);
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
            binding.buttonCatagory.setText(category.getName());
            binding.buttonCatagory.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else {
            binding.buttonCatagory.setText(R.string.no_category);
            binding.buttonCatagory.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
        }

        if (selectedTask.getDueDate() != null) {
            binding.dueDateField.setText(selectedTask.getDueDate().toString());
        }

        if (selectedTask.getDueTime() != null) {
            binding.dueTimeField.setText(selectedTask.getDueTime().toString());
        }

        Note note = noteDAOImpl.getNoteByTaskID(selectedTask.getTaskID());
        if (note != null) {
            binding.noteField.setText(R.string.edit);
        }
    }

    private void setEvents() {
        binding.buttonCatagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryPopupMenu(view);
            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.titleTaskField.requestFocus();
                binding.titleTaskField.clearFocus();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        binding.titleTaskField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                selectedTask.setTitle(binding.titleTaskField.getText().toString());
                taskDAOImpl.updateTask(selectedTask);
            }
        });

        binding.buttonAddSubtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subtask subtask = new Subtask();
                subtask.setSubtaskID(IDGenerator.generateSubTaskID());
                subtask.setTaskID(selectedTask.getTaskID());

                subtaskAdapter.addSubtask(subtask);
                binding.subTaskList.scrollToPosition(subtaskList.size() - 1);
            }
        });

        binding.dueDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialogFragment datePicker = DateDialogFragment.newInstance();
                datePicker.setOnDateSelectedListener(UpdateTaskActivity.this);
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        binding.dueTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaterialTimePicker();
            }
        });

        binding.noteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTaskActivity.this, AddOrEditNoteActivity.class);
                intent.putExtra("task", selectedTask);
                addNotesLauncher.launch(intent);
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
                binding.buttonCatagory.setText(item.getTitle());
                if (selectedCategoryID != 0) {
                    binding.buttonCatagory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                } else {
                    binding.buttonCatagory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_text));
                }
                selectedTask.setCategoryID(selectedCategoryID);
                taskDAOImpl.updateTask(selectedTask);
                return true;
            }
        });
        popupMenu.show();
    }

    private void showMaterialTimePicker() {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
                .setTitleText("Set Time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(7)
                .setMinute(0)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTheme(R.style.CustomMaterialTimePicker);

        MaterialTimePicker picker = builder.build();
        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                LocalTime selectedTime = LocalTime.of(hour, minute);
                binding.dueTimeField.setText(selectedTime.toString());
                selectedTask.setDueTime(selectedTime);
                taskDAOImpl.updateTask(selectedTask);
            }
        });
    }

    @Override
    public void onDateSelected(LocalDate date) {
        if (date != null){
            binding.dueDateField.setText(date.toString());
        } else {
            binding.dueDateField.setText(R.string.no_date);
        }
        selectedTask.setDueDate(date);
        taskDAOImpl.updateTask(selectedTask);
    }
}