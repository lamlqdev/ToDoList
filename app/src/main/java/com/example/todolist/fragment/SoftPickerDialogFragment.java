package com.example.todolist.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentSoftPickerDialogBinding;

public class SoftPickerDialogFragment extends DialogFragment {
    private FragmentSoftPickerDialogBinding binding;
    private String currentSortOption;
    private OnSortOptionSelectedListener listener;
    public interface OnSortOptionSelectedListener {
        void onSortOptionSelected(String sortBy);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSoftPickerDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentSortOption = getArguments().getString("currentSortOption", "dueDate");
        }

        if ("dueDate".equals(currentSortOption)) {
            binding.byDueDate.setChecked(true);
        } else if ("createTime".equals(currentSortOption)) {
            binding.byCreateTime.setChecked(true);
        } else if ("alphabetical".equals(currentSortOption)) {
            binding.byAlphabetical.setChecked(true);
        }

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonDone.setOnClickListener(v -> {
            if (listener != null) {
                String sortBy = null;
                if (binding.byDueDate.isChecked()) {
                    sortBy = "dueDate";
                } else if (binding.byCreateTime.isChecked()) {
                    sortBy = "createTime";
                } else if (binding.byAlphabetical.isChecked()) {
                    sortBy = "alphabetical";
                }
                listener.onSortOptionSelected(sortBy);
            }
            dismiss();
        });
    }

    public static SoftPickerDialogFragment newInstance(String currentSortOption) {
        SoftPickerDialogFragment fragment = new SoftPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentSortOption", currentSortOption);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int widthInPixels = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int heightInPixels = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(widthInPixels, heightInPixels);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setOnSortOptionSelectedListener(OnSortOptionSelectedListener listener) {
        this.listener = listener;
    }
}