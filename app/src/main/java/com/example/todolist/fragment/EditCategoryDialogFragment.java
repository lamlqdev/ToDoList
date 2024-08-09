package com.example.todolist.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentEditCategoryDialogBinding;
import com.example.todolist.model.Category;


public class EditCategoryDialogFragment extends DialogFragment {
    private FragmentEditCategoryDialogBinding binding;
    private static final String ARG_CATEGORY_SELECTED = "category_selected";
    private Category categorySelected;
    private onCategoryEditedListener onCategoryEditedListener;

    public interface onCategoryEditedListener {
        void onCategoryEdited(Category category);
    }

    public static EditCategoryDialogFragment newInstance(Category category) {
        EditCategoryDialogFragment fragment = new EditCategoryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY_SELECTED, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditCategoryDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categorySelected = (Category) getArguments().getSerializable(ARG_CATEGORY_SELECTED);
        }

        binding.editTextCategoryName.setText(categorySelected.getName());
        binding.editTextCategoryName.requestFocus();

        binding.charCountTextView.setText(categorySelected.getName().length() + "/50");

        int color = ContextCompat.getColor(requireContext(), R.color.blue);
        GradientDrawable drawable = (GradientDrawable) binding.categoryColor.getBackground();
        drawable.setColor(color);

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

        binding.chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.listColor.setVisibility(View.VISIBLE);
            }
        });

        setupColorSelection();

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
                    if (onCategoryEditedListener != null) {
                        categorySelected.setName(categoryName);
                        onCategoryEditedListener.onCategoryEdited(categorySelected);
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

    private void setupColorSelection() {
        binding.categoryRedColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#F05422"));
        });
        binding.categoryYellowColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#F3C637"));
        });
        binding.categoryGreenColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#82AD44"));
        });
        binding.categoryDarkGreenColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#2D9185"));
        });
        binding.categoryBlueColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#3290E7"));
        });
        binding.categoryPurpleColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.GONE);
            updateCategoryColor(Color.parseColor("#B59BDC"));
        });
        binding.categoryDefaultColor.setOnClickListener(view -> {
            binding.categoryDefaultText.setVisibility(View.VISIBLE);
            updateCategoryColor(Color.parseColor("#4485E9"));
        });
    }

    private void updateCategoryColor(int color) {
        GradientDrawable drawable = (GradientDrawable) binding.categoryColor.getBackground();
        drawable.setColor(color);
        categorySelected.setColor(color);
    }

    public void setOnCategoryEditedListener(onCategoryEditedListener listener) {
        this.onCategoryEditedListener = listener;
    }
}