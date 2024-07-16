package com.example.todolist.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentMinutePickerDialogBinding;

public class MinutePickerDialogFragment extends DialogFragment {

    private FragmentMinutePickerDialogBinding binding;
    private String[] displayedValues;
    private OnMinuteSelectedListener onMinuteSelectedListener;

    public interface OnMinuteSelectedListener {
        void onMinuteSelected(int selectedMinutes);
    }

    public static MinutePickerDialogFragment newInstance() {
        return new MinutePickerDialogFragment();
    }

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
        binding.numberMinutePicker.setValue(2);

        binding.buttonCancel.setOnClickListener(v -> {
            if (onMinuteSelectedListener != null) {
                onMinuteSelectedListener.onMinuteSelected(0);
            }
            dismiss();
        });

        binding.buttonDone.setOnClickListener(v -> {
            int selectedMinutes = (binding.numberMinutePicker.getValue() + 1) * 5;
            if (onMinuteSelectedListener != null) {
                onMinuteSelectedListener.onMinuteSelected(selectedMinutes);
            }
            dismiss();
        });
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

    public void setOnMinuteSelectedListener(OnMinuteSelectedListener listener){
        this.onMinuteSelectedListener = listener;
    }
}
