package com.example.todolist.fragment;

import android.content.Context;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.MenuCategoryAdapter;
import com.example.todolist.databinding.FragmentBottomSheetAddTaskBinding;
import com.example.todolist.model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class BottomSheetAddTaskFragment extends BottomSheetDialogFragment {
    private FragmentBottomSheetAddTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Category> categoryList;
    public BottomSheetAddTaskFragment() {
        // Required empty public constructor
    }

    public static BottomSheetAddTaskFragment newInstance() {
        return new BottomSheetAddTaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomSheetAddTaskBinding.inflate(inflater, container, false);
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        categoryList = categoryDAOImpl.getAllCategories();
        setWidgets();
        setEvents();
        return binding.getRoot();
    }

    private void setEvents() {
        binding.buttonAddCatagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryPopupMenu(view);
            }
        });
    }

    private void setWidgets() {
        binding.titleTaskField.requestFocus();
    }

    private void showCategoryPopupMenu(View view) {
        Context wrapper = new ContextThemeWrapper(requireContext(), R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        for(int i = 0; i < categoryList.size(); i++) {
            popupMenu.getMenu().add(categoryList.get(i).getName());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                binding.buttonAddCatagory.setText(item.getTitle());
                return true;
            }
        });
        popupMenu.show();
    }
}