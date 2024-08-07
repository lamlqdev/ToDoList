package com.example.todolist.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentAddCategoryDialogBinding;

public class AddCategoryDialogFragment extends DialogFragment {

    private FragmentAddCategoryDialogBinding binding;
    private OnCategoryAddedListener onCategoryAddedListener;

    public interface OnCategoryAddedListener {
        void onCategoryAdded(String categoryName);
    }

    public static AddCategoryDialogFragment newInstance() {
        return new AddCategoryDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCategoryDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editTextCategoryName.requestFocus();

        binding.editTextCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                binding.charCountTextView.setText(length + "/50");

                if (length == 50) {
                    binding.charCountTextView.setTextColor(Color.RED);
                    binding.errorTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.charCountTextView.setTextColor(Color.BLACK);
                    binding.errorTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = binding.editTextCategoryName.getText().toString();
                if (!categoryName.isEmpty()) {
                    if (categoryName.equalsIgnoreCase("All")) {
                        Toast.makeText(requireContext(), "Category can not be named 'All'", Toast.LENGTH_SHORT).show();
                        binding.editTextCategoryName.setText("");
                        binding.editTextCategoryName.requestFocus();
                        return;
                    }
                    if (onCategoryAddedListener != null) {
                        onCategoryAddedListener.onCategoryAdded(categoryName);
                    }
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
                    binding.editTextCategoryName.requestFocus();
                }
            }
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

    public void setOnCategoryAddedListener(OnCategoryAddedListener listener) {
        this.onCategoryAddedListener = listener;
    }
}