package com.example.todolist.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.CategoryAdapter;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.example.todolist.utils.TaskCategorizer;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener {
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

        setWidget();
        setEvents();
        return binding.getRoot();
    }

    private void setWidget() {
        setRecyclerViewCategory();
        setRecyclerTask();
    }

    private void setRecyclerTask() {
        taskList = taskDAOImpl.getAllTasks();
        TaskCategorizer.categorizeTasks(taskList, previousTasks, todayTasks, futureTasks, completedTasks);

        previousTaskAdapter = new TaskAdapter(getContext(), previousTasks);
        todayTaskAdapter = new TaskAdapter(getContext(), todayTasks);
        futureTaskAdapter = new TaskAdapter(getContext(), futureTasks);
        completedTaskAdapter = new TaskAdapter(getContext(), completedTasks);

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
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);

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

        binding.floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetAddTaskFragment bottomSheet = BottomSheetAddTaskFragment.newInstance();
                bottomSheet.setOnTaskAddedListener(TasksFragment.this);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    @Override
    public void onTaskAdded() {
        taskList = taskDAOImpl.getAllTasks();
        previousTasks.clear();
        todayTasks.clear();
        futureTasks.clear();
        completedTasks.clear();
        TaskCategorizer.categorizeTasks(taskList, previousTasks, todayTasks, futureTasks, completedTasks);

        if (!previousTasks.isEmpty()) {
            previousTaskAdapter.notifyDataSetChanged();
            binding.previousTaskContainer.setVisibility(View.VISIBLE);
        } else {
            binding.previousTaskContainer.setVisibility(View.GONE);
        }

        if (!todayTasks.isEmpty()) {
            binding.todayTaskContainer.setVisibility(View.VISIBLE);
            todayTaskAdapter.notifyDataSetChanged();
        } else {
            binding.todayTaskContainer.setVisibility(View.GONE);
        }

        if (!futureTasks.isEmpty()) {
            futureTaskAdapter.notifyDataSetChanged();
            binding.futureTaskContainer.setVisibility(View.VISIBLE);
        } else {
            binding.futureTaskContainer.setVisibility(View.GONE);
        }

        if (!completedTasks.isEmpty()) {
            completedTaskAdapter.notifyDataSetChanged();
            binding.completedTaskContainer.setVisibility(View.VISIBLE);
        } else {
            binding.completedTaskContainer.setVisibility(View.GONE);
        }
    }

}