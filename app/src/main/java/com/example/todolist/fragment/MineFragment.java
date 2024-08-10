package com.example.todolist.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.Next7DayTaskAdapter;
import com.example.todolist.databinding.FragmentMineBinding;
import com.example.todolist.model.Task;

import java.util.List;

public class MineFragment extends Fragment {
    private FragmentMineBinding binding;
    private TaskDAOImpl taskDAOImpl;
    private List<Task> next7DayTaskList;
    private Next7DayTaskAdapter next7DayTaskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        initializeData();
        setWidget();
        setEvents();
        return binding.getRoot();
    }

    private void setWidget() {
        List<Task> completedTasks = taskDAOImpl.getAllCompletedTasks();
        List<Task> allTasks = taskDAOImpl.getAllTasks();
        int numberCompletedTask = completedTasks.size();
        int numberPendingTask = allTasks.size() - numberCompletedTask;

        binding.numberCompletedTask.setText(String.valueOf(numberCompletedTask));
        binding.numberPendingTask.setText(String.valueOf(numberPendingTask));

        next7DayTaskList = taskDAOImpl.getTasksForNext7Days();
        next7DayTaskAdapter = new Next7DayTaskAdapter(getContext(), next7DayTaskList);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewTasks.setAdapter(next7DayTaskAdapter);
    }

    private void setEvents() {

    }

    private void initializeData() {
        taskDAOImpl = new TaskDAOImpl(getContext());
    }
}