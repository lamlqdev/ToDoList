package com.example.todolist.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.CategoryAdapter;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;

import java.util.List;

public class TasksFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener {
    private FragmentTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private TaskDAOImpl taskDAOImpl;
    private List<Category> categoryList;
    private List<Task> taskList;
    private CategoryAdapter categoryAdapter;
    private TaskAdapter taskAdapter;

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
        binding.previousTaskContainer.setVisibility(View.GONE);
        binding.futureTaskContainer.setVisibility(View.GONE);
    }

    private void setRecyclerTask() {
        taskList = taskDAOImpl.getAllTasks();
        taskAdapter = new TaskAdapter(getContext(), taskList);

        binding.listTodayTasks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.listTodayTasks.setAdapter(taskAdapter);
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
        taskList.clear();
        taskList.addAll(0, taskDAOImpl.getAllTasks());
        taskAdapter.notifyDataSetChanged();
        binding.listTodayTasks.smoothScrollToPosition(0);
    }
}