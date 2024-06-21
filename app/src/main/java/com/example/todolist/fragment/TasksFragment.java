package com.example.todolist.fragment;

import android.content.Context;
import android.content.Intent;
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
import android.widget.PopupMenu;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.R;
import com.example.todolist.activity.UpdateTaskActivity;
import com.example.todolist.adapter.CategoryAdapter;
import com.example.todolist.databinding.FragmentTaskBinding;
import com.example.todolist.model.Category;

import java.util.List;

public class TasksFragment extends Fragment {
    private FragmentTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private RecyclerView categoryContainer;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        setWidget();
        setRecyclerViewCategory();
        setEvents();
        return binding.getRoot();
    }
    private void setWidget() {

    }
    private void setRecyclerViewCategory() {
        categoryContainer = binding.categoryContainer;
        categoryList = categoryDAOImpl.getAllCategories();

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        categoryContainer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryContainer.setAdapter(categoryAdapter);
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
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
    }
}