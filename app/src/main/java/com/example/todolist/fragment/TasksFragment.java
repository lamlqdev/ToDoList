package com.example.todolist.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener, TaskAdapter.OnTaskInteractionListener, CategorySelectorAdapter.OnClickListener, SoftPickerDialogFragment.OnSortOptionSelectedListener{
    private FragmentTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private TaskDAOImpl taskDAOImpl;
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
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        taskDAOImpl = new TaskDAOImpl(getContext());
        taskList = taskDAOImpl.getAllTasks();
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

        setWidget();
        setEvents();
        return binding.getRoot();
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

    private void setupTaskRecyclerView(RecyclerView recyclerView, List<Task> tasks, TaskAdapter adapter, View container) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        if (!tasks.isEmpty()) {
            container.setVisibility(View.VISIBLE);
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private void setRecyclerViewCategory() {
        categoryList = categoryDAOImpl.getAllCategories();
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
                                    List<Task> allTasks = taskDAOImpl.getAllTasks();
                                    clearAllTaskLists();
                                    setRecyclerViewTask(allTasks);
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
    public void onItemTaskClick(Task task) {
        Intent intent = new Intent(requireContext(), UpdateTaskActivity.class);
        intent.putExtra("task", task);
        updateTaskLauncher.launch(intent);
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
}