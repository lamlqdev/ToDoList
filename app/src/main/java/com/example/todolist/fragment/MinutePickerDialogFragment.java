package com.example.todolist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.databinding.FragmentMinutePickerDialogBinding;

public class MinutePickerDialogFragment extends DialogFragment {

    private FragmentMinutePickerDialogBinding binding;
    private String[] displayedValues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMinutePickerDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayedValues = new String[36];
        for (int i = 0; i < displayedValues.length; i++) {
            displayedValues[i] = String.valueOf((i + 1) * 5);
        }

        binding.numberMinutePicker.setMinValue(0);
        binding.numberMinutePicker.setMaxValue(displayedValues.length - 1);
        binding.numberMinutePicker.setDisplayedValues(displayedValues);
        binding.numberMinutePicker.setValue(4);

        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonDone.setOnClickListener(v -> {
            int selectedMinutes = binding.numberMinutePicker.getValue();
            // Do something with selectedMinutes
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
