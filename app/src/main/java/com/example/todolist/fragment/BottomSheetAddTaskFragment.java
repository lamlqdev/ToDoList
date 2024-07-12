package com.example.todolist.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.SubtaskDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.MenuCategoryAdapter;
import com.example.todolist.adapter.SubtaskAdapter;
import com.example.todolist.databinding.FragmentBottomSheetAddTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Subtask;
import com.example.todolist.model.Task;
import com.example.todolist.utils.TaskIDGenerator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetAddTaskFragment extends BottomSheetDialogFragment implements DateDialogFragment.OnDateSelectedListener{
    private FragmentBottomSheetAddTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private SubtaskDAOImpl subtaskDAOImpl;
    private TaskDAOImpl taskDAOImpl;
    private List<Category> categoryList;
    private SubtaskAdapter subtaskAdapter;
    private List<Subtask> subTaskList;
    private int taskID;
    private LocalDate selectedDate = LocalDate.now();
    private LocalTime selectedTime;
    private int selectedCategoryID = 0;
    private static final String ARG_CATEGORY_SELECTED = "category_selected";
    private String categorySelected;
    private OnTaskAddedListener onTaskAddedListener;

    public interface OnTaskAddedListener {
        void onTaskAdded(Task newTask);
    }

    public BottomSheetAddTaskFragment() {
    }

    public static BottomSheetAddTaskFragment newInstance(String categorySelected) {
        BottomSheetAddTaskFragment fragment = new BottomSheetAddTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_SELECTED, categorySelected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskID = TaskIDGenerator.generateUniqueID();
        if (getArguments() != null) {
            categorySelected = getArguments().getString(ARG_CATEGORY_SELECTED);
            if (categorySelected.equals("All")) {
                categorySelected = "No Category";
            }
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setDraggable(false);
            }
        });

        return dialog;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetAddTaskBinding.inflate(inflater, container, false);
        setWidgets();
        setEvents();
        return binding.getRoot();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        subtaskDAOImpl.deleteSubtaskByTaskID(taskID);
        subTaskList.clear();
        dismiss();
        super.onCancel(dialog);
    }

    private void setWidgets() {
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        subtaskDAOImpl = new SubtaskDAOImpl(getContext());
        taskDAOImpl = new TaskDAOImpl(getContext());

        categoryList = categoryDAOImpl.getAllCategories();
        subTaskList = new ArrayList<>();
        subtaskAdapter = new SubtaskAdapter(getContext(), subTaskList);

        binding.recyclerViewSubTask.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSubTask.setAdapter(subtaskAdapter);

        binding.buttonAddCatagory.setText(categorySelected);
        if (!categorySelected.equals("No Category")){
            binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        }
        binding.titleTaskField.requestFocus();
    }
    private void setEvents() {
        binding.buttonAddCatagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryPopupMenu(view);
            }
        });

        binding.subTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recyclerViewSubTask.setVisibility(View.VISIBLE);

                Subtask subtask = new Subtask();
                subtask.setTaskID(taskID);

                subtaskAdapter.addSubtask(subtask);
                binding.recyclerViewSubTask.scrollToPosition(subTaskList.size() - 1);
            }
        });

        binding.calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialogFragment datePicker = DateDialogFragment.newInstance();
                datePicker.setOnDateSelectedListener(BottomSheetAddTaskFragment.this);
                datePicker.show(getParentFragmentManager(), "datePicker");
            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaterialTimePicker();
            }
        });

        binding.titleTaskField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.buttonCreateTask.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                } else {
                    binding.buttonCreateTask.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.buttonCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });
    }

    public void setOnTaskAddedListener(OnTaskAddedListener listener){
        this.onTaskAddedListener = listener;
    }

    private void addNewTask() {
        String taskTitle = binding.titleTaskField.getText().toString().trim();
        if (taskTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a task title", Toast.LENGTH_SHORT).show();
        } else {
            Task newTask = new Task();
            newTask.setTaskID(taskID);
            newTask.setTitle(taskTitle);
            newTask.setDueDate(selectedDate);

            if (selectedCategoryID != 0) {
                newTask.setCategoryID(selectedCategoryID);
            } else {
                selectedCategoryID = categoryDAOImpl.getIDByCategoryName(categorySelected);
                newTask.setCategoryID(selectedCategoryID);
            }

            if (selectedTime != null) {
                newTask.setDueTime(selectedTime);
            }

            taskDAOImpl.addTask(newTask);
            if (onTaskAddedListener != null) {
                onTaskAddedListener.onTaskAdded(newTask);
            }

            refreshInput();
        }
    }

    private void refreshInput() {
        //clear input and generate new taskID
        taskID = TaskIDGenerator.generateUniqueID();
        binding.titleTaskField.setText("");
        selectedDate = LocalDate.now();
        selectedTime = null;
        binding.buttonAddCatagory.setText(categorySelected);
        if (!categorySelected.equals("No Category")){
            binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        } else{
            binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text));
        }
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
        picker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                selectedTime = LocalTime.of(hour, minute);
            }
        });
    }

    private void showCategoryPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(requireContext(), R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        for(int i = 0; i < categoryList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i+1, Menu.NONE, categoryList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedCategoryID = item.getItemId();
                binding.buttonAddCatagory.setText(item.getTitle());
                binding.buttonAddCatagory.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onDateSelected(LocalDate date) {
        if (date != null){
            selectedDate = date;
        }
        else {
            selectedDate = null;
        }
    }
}