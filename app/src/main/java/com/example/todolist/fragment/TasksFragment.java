package com.example.todolist.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.activity.UpdateTaskActivity;
import com.example.todolist.adapter.CategoryAdapter;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.example.todolist.utils.TaskCategorizer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener, TaskAdapter.OnTaskInteractionListener, CategoryAdapter.OnClickListener{
    private FragmentTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private TaskDAOImpl taskDAOImpl;
    private List<Category> categoryList = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private List<Task> previousTasks = new ArrayList<>();
    private List<Task> todayTasks = new ArrayList<>();
    private List<Task> futureTasks = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private TaskAdapter previousTaskAdapter;
    private TaskAdapter todayTaskAdapter;
    private TaskAdapter futureTaskAdapter;
    private TaskAdapter completedTaskAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        taskDAOImpl = new TaskDAOImpl(getContext());
        taskList = taskDAOImpl.getAllTasks();
        setWidget();
        setEvents();
        return binding.getRoot();
    }

    private void setWidget() {
        setRecyclerViewCategory();
        setRecyclerViewTask(taskList);
    }

    private void setRecyclerViewTask(List<Task> taskList) {
        TaskCategorizer.categorizeTasks(taskList, previousTasks, todayTasks, futureTasks, completedTasks);

        previousTaskAdapter = new TaskAdapter(getContext(), previousTasks, this);
        todayTaskAdapter = new TaskAdapter(getContext(), todayTasks, this);
        futureTaskAdapter = new TaskAdapter(getContext(), futureTasks, this);
        completedTaskAdapter = new TaskAdapter(getContext(), completedTasks, this);

        setupTaskRecyclerView(binding.listPreviousTasks, previousTasks, previousTaskAdapter, binding.previousTaskContainer);
        setupTaskRecyclerView(binding.listTodayTasks, todayTasks, todayTaskAdapter, binding.todayTaskContainer);
        setupTaskRecyclerView(binding.listFutureTasks, futureTasks, futureTaskAdapter, binding.futureTaskContainer);
        setupTaskRecyclerView(binding.listCompletedTasks, completedTasks, completedTaskAdapter, binding.completedTaskContainer);
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
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);

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
                popup.show();
            }
        });

        binding.floatingAddButton.setOnClickListener(v -> {
            BottomSheetAddTaskFragment bottomSheet = BottomSheetAddTaskFragment.newInstance();
            bottomSheet.setOnTaskAddedListener(TasksFragment.this);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        setupArrowButton(binding.previousArrowButton, binding.listPreviousTasks, "previous_tasks_expanded");
        setupArrowButton(binding.todayArrowButton, binding.listTodayTasks, "today_tasks_expanded");
        setupArrowButton(binding.futureArrowButton, binding.listFutureTasks, "future_tasks_expanded");
        setupArrowButton(binding.completedArrowButton, binding.listCompletedTasks, "completed_tasks_expanded");
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
        }
    }
    private void addToTaskList(List<Task> taskList, TaskAdapter taskAdapter, Task task, View container) {
        int position = taskList.size();
        taskList.add(task);
        if (position == 0) {
            taskAdapter.notifyDataSetChanged();
        } else {
            taskAdapter.notifyItemInserted(position);
        }
        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskStatusChanged() {
        previousTasks.clear();
        todayTasks.clear();
        futureTasks.clear();
        completedTasks.clear();
        taskList = taskDAOImpl.getAllTasks();
        setRecyclerViewTask(taskList);
    }

    @Override
    public void onItemTaskClick(int position) {
        Intent intent = new Intent(requireContext(), UpdateTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemCategoryClick(String categoryName) {
        previousTasks.clear();
        todayTasks.clear();
        futureTasks.clear();
        completedTasks.clear();
        if(categoryName.equals("All")){
            taskList = taskDAOImpl.getAllTasks();
        }else{
            taskList = taskDAOImpl.getTasksByCategoryName(categoryName);
        }
        setRecyclerViewTask(taskList);
    }
}