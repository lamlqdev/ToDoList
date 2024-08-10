package com.example.todolist.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.activity.AddOrEditCategoryActivity;
import com.example.todolist.activity.TimeLineCompletedTaskActivity;
import com.example.todolist.activity.UpdateTaskActivity;
import com.example.todolist.adapter.CategorySelectorAdapter;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.example.todolist.utils.TaskCategorizer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TasksFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener, TaskAdapter.OnTaskInteractionListener, CategorySelectorAdapter.OnClickListener, SoftPickerDialogFragment.OnSortOptionSelectedListener, DatePickerDialogFragment.OnDateSelectedListener{
    private FragmentTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private TaskDAOImpl taskDAOImpl;
    private Task currentTask;
    private String categorySelected = "All";
    private String sortBySelected = "dueDate";
    private List<Category> categoryList = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private List<Task> previousTasks = new ArrayList<>();
    private List<Task> todayTasks = new ArrayList<>();
    private List<Task> futureTasks = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();
    private CategorySelectorAdapter categoryAdapter;
    private TaskAdapter previousTaskAdapter;
    private TaskAdapter todayTaskAdapter;
    private TaskAdapter futureTaskAdapter;
    private TaskAdapter completedTaskAdapter;
    private ActivityResultLauncher<Intent> updateTaskLauncher;
    private ActivityResultLauncher<Intent> deleteCompletedTaskLauncher;
    private ActivityResultLauncher<Intent> manageCategoryLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        sortBySelected = getPreferences().getString("sortBySelected", "dueDate");

        updateTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == UpdateTaskActivity.RESULT_OK) {
                    clearAllTaskLists();
                    if (categorySelected.equals("All")) {
                        taskList = taskDAOImpl.getAllTasks();
                    } else {
                        taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
                    }
                    setRecyclerViewTask(taskList);
                }
            }
        );

        deleteCompletedTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == TimeLineCompletedTaskActivity.RESULT_OK) {
                    clearAllTaskLists();
                    if (categorySelected.equals("All")) {
                        taskList = taskDAOImpl.getAllTasks();
                    } else {
                        taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
                    }
                    setRecyclerViewTask(taskList);
                }
            }
        );

        manageCategoryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AddOrEditCategoryActivity.RESULT_OK) {
                    clearAllTaskLists();
                    categoryList.clear();
                    categorySelected = "All";
                    taskList = taskDAOImpl.getAllTasks();
                    setRecyclerViewTask(taskList);
                    setRecyclerViewCategory();
                }
            }
        );

        setupSwipeMenuAndListener(binding.listPreviousTasks, previousTasks);
        setupSwipeMenuAndListener(binding.listTodayTasks, todayTasks);
        setupSwipeMenuAndListener(binding.listFutureTasks, futureTasks);
        setupSwipeMenuAndListener(binding.listCompletedTasks, completedTasks);

        initializeData();
        setWidget();
        setEvents();
        return binding.getRoot();
    }

    private void setupSwipeMenuAndListener(SwipeRecyclerView recyclerView, List<Task> tasks) {
        recyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem editDateItem = new SwipeMenuItem(requireContext())
                        .setText("Date")
                        .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
                        .setImage(R.drawable.ic_calendar_white)
                        .setTextColor(Color.WHITE)
                        .setTextSize(12)
                        .setWidth(170)
                        .setHeight(160);
                rightMenu.addMenuItem(editDateItem);

                SwipeMenuItem editTimeItem = new SwipeMenuItem(requireContext())
                        .setText("Time")
                        .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
                        .setImage(R.drawable.ic_schedule_white)
                        .setTextColor(Color.WHITE)
                        .setTextSize(12)
                        .setWidth(170)
                        .setHeight(160);
                rightMenu.addMenuItem(editTimeItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(requireContext())
                        .setText("Delete")
                        .setBackground(R.drawable.background_delete_swipe_menu_item)
                        .setImage(R.drawable.ic_delete_white)
                        .setTextColor(Color.WHITE)
                        .setTextSize(12)
                        .setWidth(170)
                        .setHeight(160);
                rightMenu.addMenuItem(deleteItem);
            }
        });

        recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                menuBridge.closeMenu();
                int menuPosition = menuBridge.getPosition();
                currentTask = tasks.get(position);

                if (menuPosition == 0) {
                    DatePickerDialogFragment datePicker = DatePickerDialogFragment.newInstance(currentTask.getDueDate());
                    datePicker.setOnDateSelectedListener(TasksFragment.this);
                    datePicker.show(getChildFragmentManager(), "datePicker");
                } else if (menuPosition == 1){
                    int hour = 7;
                    int minute = 0;
                    LocalTime dueTime = currentTask.getDueTime();
                    if (dueTime != null) {
                        hour = dueTime.getHour();
                        minute = dueTime.getMinute();
                    }
                    MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
                            .setTitleText("Set Time")
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(hour)
                            .setMinute(minute)
                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                            .setTheme(R.style.CustomMaterialTimePicker);

                    MaterialTimePicker picker = builder.build();
                    picker.show(getChildFragmentManager(), "MATERIAL_TIME_PICKER");

                    picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int hour = picker.getHour();
                            int minute = picker.getMinute();
                            LocalTime selectedTime = LocalTime.of(hour, minute);
                            currentTask.setDueTime(selectedTime);
                            taskDAOImpl.updateTask(currentTask);
                            recyclerView.getAdapter().notifyItemChanged(position);
                        }
                    });
                } else {
                    new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setTitle("Delete Task")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                taskDAOImpl.deleteTask(currentTask);
                                tasks.remove(position);
                                recyclerView.getAdapter().notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                }
            }
        });

        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Task updateTask = tasks.get(position);
                Intent intent = new Intent(requireContext(), UpdateTaskActivity.class);
                intent.putExtra("task", updateTask);
                updateTaskLauncher.launch(intent);
            }
        });
    }

    private void initializeData() {
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        taskDAOImpl = new TaskDAOImpl(getContext());
        taskList = taskDAOImpl.getAllTasks();
    }

    private void setWidget() {
        setRecyclerViewCategory();
        setRecyclerViewTask(taskList);

        String text = binding.textViewCheckAllCompletedTasks.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.textViewCheckAllCompletedTasks.setText(spannableString);

    }

    private void setRecyclerViewTask(List<Task> taskList) {
        Iterator<Task> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getCategoryID() != -1) {
                Category category = categoryDAOImpl.getCategory(task.getCategoryID());
                if (category != null && !category.isVisible()) {
                    iterator.remove();
                }
            }
        }
        TaskCategorizer.categorizeTasks(taskList, previousTasks, todayTasks, futureTasks, completedTasks);

        TaskCategorizer.sortTasks(previousTasks, sortBySelected);
        TaskCategorizer.sortTasks(todayTasks, sortBySelected);
        TaskCategorizer.sortTasks(futureTasks, sortBySelected);
        TaskCategorizer.sortTasks(completedTasks, sortBySelected);

        previousTaskAdapter = new TaskAdapter(getContext(), previousTasks, this);
        todayTaskAdapter = new TaskAdapter(getContext(), todayTasks, this);
        futureTaskAdapter = new TaskAdapter(getContext(), futureTasks, this);
        completedTaskAdapter = new TaskAdapter(getContext(), completedTasks, this);

        setupTaskRecyclerView(binding.listPreviousTasks, previousTasks, previousTaskAdapter, binding.previousTaskContainer);
        setupTaskRecyclerView(binding.listTodayTasks, todayTasks, todayTaskAdapter, binding.todayTaskContainer);
        setupTaskRecyclerView(binding.listFutureTasks, futureTasks, futureTaskAdapter, binding.futureTaskContainer);
        setupTaskRecyclerView(binding.listCompletedTasks, completedTasks, completedTaskAdapter, binding.completedTaskContainer);

        if (categorySelected.equals("All")){
            List<Task> completedTaskList = taskDAOImpl.getAllCompletedTasks();
            if (completedTaskList.isEmpty()) {
                binding.textViewCheckAllCompletedTasks.setVisibility(View.GONE);
            } else {
                binding.textViewCheckAllCompletedTasks.setVisibility(View.VISIBLE);
            }
        } else {
            List<Task> completedTaskList = taskDAOImpl.getCompletedTasksByCategoryName(categorySelected);
            if (completedTaskList.isEmpty()) {
                binding.textViewCheckAllCompletedTasks.setVisibility(View.GONE);
            } else {
                binding.textViewCheckAllCompletedTasks.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupTaskRecyclerView(SwipeRecyclerView recyclerView, List<Task> tasks, TaskAdapter adapter, View container) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        if (!tasks.isEmpty()) {
            container.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private void setRecyclerViewCategory() {
        categoryList = categoryDAOImpl.getAllVisibleCategories();
        categoryAdapter = new CategorySelectorAdapter(getContext(), categoryList, this);

        binding.categoryContainer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoryContainer.setAdapter(categoryAdapter);
        categoryAdapter.setSelectedItem(0);
    }

    private void setEvents() {
        binding.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(requireContext(), R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.tasks_fragment_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.sort_by) {
                        SoftPickerDialogFragment softPickerDialogFragment = SoftPickerDialogFragment.newInstance(sortBySelected);
                        softPickerDialogFragment.setOnSortOptionSelectedListener(TasksFragment.this);
                        softPickerDialogFragment.show(getParentFragmentManager(), "SortPickerDialogFragment");
                    }

                    if (item.getItemId() == R.id.search){
                        binding.searchContainer.setVisibility(View.VISIBLE);
                        binding.categoryManagerContainer.setVisibility(View.INVISIBLE);

                        binding.autoCompleteTextViewSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.toString().trim().isEmpty()) {
                                    binding.buttonClear.setVisibility(View.GONE);
                                    clearAllTaskLists();
                                    if (categorySelected.equals("All")) {
                                        taskList = taskDAOImpl.getAllTasks();
                                    } else {
                                        taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
                                    }
                                    setRecyclerViewTask(taskList);
                                    return;
                                } else {
                                    binding.buttonClear.setVisibility(View.VISIBLE);
                                }

                                List<Task> resultList = taskDAOImpl.searchTasksByName(s.toString());
                                clearAllTaskLists();
                                setRecyclerViewTask(resultList);
                            }
                        });

                        binding.buttonClear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.autoCompleteTextViewSearch.setText("");
                            }
                        });

                        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.autoCompleteTextViewSearch.setText("");
                                binding.searchContainer.setVisibility(View.GONE);
                                binding.categoryManagerContainer.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    if (item.getItemId() == R.id.manage_categories){
                        Intent intent = new Intent(requireContext(), AddOrEditCategoryActivity.class);
                        manageCategoryLauncher.launch(intent);
                    }
                    return true;
                });
                popup.show();
            }
        });

        binding.floatingAddButton.setOnClickListener(v -> {
            BottomSheetAddTaskFragment bottomSheet = BottomSheetAddTaskFragment.newInstance(categorySelected);
            bottomSheet.setOnTaskAddedListener(TasksFragment.this);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        setupArrowButton(binding.previousArrowButton, binding.listPreviousTasks, "previous_tasks_expanded");
        setupArrowButton(binding.todayArrowButton, binding.listTodayTasks, "today_tasks_expanded");
        setupArrowButton(binding.futureArrowButton, binding.listFutureTasks, "future_tasks_expanded");
        setupArrowButton(binding.completedArrowButton, binding.listCompletedTasks, "completed_tasks_expanded");

        binding.textViewCheckAllCompletedTasks.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TimeLineCompletedTaskActivity.class);
            intent.putExtra("Current Category", categorySelected);
            deleteCompletedTaskLauncher.launch(intent);
        });
    }

    private void setupArrowButton(ImageButton arrowButton, RecyclerView recyclerView, String preferenceKey) {
        final boolean[] isExpanded = {getPreferences().getBoolean(preferenceKey, true)}; // Default to true if not found

        recyclerView.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
        arrowButton.setImageResource(isExpanded[0] ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);

        arrowButton.setOnClickListener(v -> {
            isExpanded[0] = !isExpanded[0];
            getPreferences().edit().putBoolean(preferenceKey, isExpanded[0]).apply();

            recyclerView.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
            arrowButton.setImageResource(isExpanded[0] ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);
        });
    }

    private SharedPreferences getPreferences() {
        return requireContext().getSharedPreferences("task_fragment_prefs", Context.MODE_PRIVATE); //keep user mode
    }

    @Override
    public void onTaskAdded(Task task) {
        LocalDate today = LocalDate.now();
        if (task.getDueDate() != null) {
            if (task.getDueDate().isBefore(today)) {
                addToTaskList(previousTasks, previousTaskAdapter, task, binding.previousTaskContainer);
            } else if (task.getDueDate().isEqual(today)) {
                addToTaskList(todayTasks, todayTaskAdapter, task, binding.todayTaskContainer);
            } else {
                addToTaskList(futureTasks, futureTaskAdapter, task, binding.futureTaskContainer);
            }
        } else {
            addToTaskList(futureTasks, futureTaskAdapter, task, binding.futureTaskContainer);
        }
    }
    private void addToTaskList(List<Task> taskList, TaskAdapter taskAdapter, Task task, View container) {
        taskList.add(task);
        TaskCategorizer.sortTasks(taskList, sortBySelected);
        taskAdapter.notifyDataSetChanged();

        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskStatusChanged() {
        int delayMillis = 170;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAllTaskLists();
                if (categorySelected.equals("All")) {
                    taskList = taskDAOImpl.getAllTasks();
                } else {
                    taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
                }
                setRecyclerViewTask(taskList);
            }
        }, delayMillis);
    }

    @Override
    public void onItemCategoryClick(String categoryName) {
        categorySelected = categoryName;
        clearAllTaskLists();

        if (categorySelected.equals("All")) {
            taskList = taskDAOImpl.getAllTasks();
        } else {
            taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
        }
        setRecyclerViewTask(taskList);
    }

    private void clearAllTaskLists() {
        previousTasks.clear();
        todayTasks.clear();
        futureTasks.clear();
        completedTasks.clear();
    }

    @Override
    public void onSortOptionSelected(String sortBy) {
        if (sortBy != null) {
            sortBySelected = sortBy;
            getPreferences().edit().putString("sortBySelected", sortBy).apply();
            clearAllTaskLists();
            if (categorySelected.equals("All")) {
                taskList = taskDAOImpl.getAllTasks();
            } else {
                taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
            }
            setRecyclerViewTask(taskList);
        }
    }

    @Override
    public void onDateSelected(LocalDate date) {
        currentTask.setDueDate(date);
        taskDAOImpl.updateTask(currentTask);
        clearAllTaskLists();
        if (categorySelected.equals("All")) {
            taskList = taskDAOImpl.getAllTasks();
        } else {
            taskList = taskDAOImpl.getTasksByCategoryName(categorySelected);
        }
        setRecyclerViewTask(taskList);
    }
}