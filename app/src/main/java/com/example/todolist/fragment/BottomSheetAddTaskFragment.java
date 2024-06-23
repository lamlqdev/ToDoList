package com.example.todolist.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.SubtaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.MenuCategoryAdapter;
import com.example.todolist.adapter.SubtaskAdapter;
import com.example.todolist.databinding.FragmentBottomSheetAddTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Subtask;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetAddTaskFragment extends BottomSheetDialogFragment {
    private FragmentBottomSheetAddTaskBinding binding;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Category> categoryList;
    private SubtaskAdapter subtaskAdapter;
    private SubtaskDAOImpl subtaskDAOImpl;
    private List<Subtask> subTaskList;
    public BottomSheetAddTaskFragment() {
    }

    public static BottomSheetAddTaskFragment newInstance() {
        return new BottomSheetAddTaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    private void setWidgets() {
        categoryDAOImpl = new CategoryDAOImpl(getContext());
        categoryList = categoryDAOImpl.getAllCategories();

        subtaskDAOImpl = new SubtaskDAOImpl(getContext());
        subTaskList = new ArrayList<>();
        subtaskAdapter = new SubtaskAdapter(getContext(), subTaskList);

        binding.recyclerViewSubTask.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSubTask.setAdapter(subtaskAdapter);

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
                subTaskList.add(subtask);
                subtaskAdapter.notifyItemInserted(subTaskList.size() - 1);
                binding.recyclerViewSubTask.scrollToPosition(subTaskList.size() - 1);
            }
        });

        binding.calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = DateDialogFragment.newInstance();
                datePicker.show(getParentFragmentManager(), "datePicker");
            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaterialTimePicker();
            }
        });
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