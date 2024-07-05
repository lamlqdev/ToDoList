package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.SubtaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListSubTaskBinding;
import com.example.todolist.model.Subtask;

import java.util.List;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder> {
    private List<Subtask> subtasks;
    private Context context;
    private LayoutInflater layoutInflater;
    private SubtaskDAOImpl subtaskDAOImpl;

    public SubtaskAdapter(Context context, List<Subtask> subtasks) {
        this.subtasks = subtasks;
        this.context = context;
        subtaskDAOImpl = new SubtaskDAOImpl(context);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SubtaskAdapter.SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListSubTaskBinding binding = ItemListSubTaskBinding.inflate(layoutInflater, parent, false);
        return new SubtaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskAdapter.SubtaskViewHolder holder, int position) {
        Subtask subtask = subtasks.get(position);
        holder.binding.checkboxIsDone.setChecked(subtask.getStatus() == 2);
        holder.binding.editTextSubTask.setText(subtask.getDescription());

        // Update subtask status on checkbox change and save to database
        holder.binding.checkboxIsDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subtask.setStatus(isChecked ? 2 : 1);
            subtaskDAOImpl.updateSubtask(subtask);
        });

        // Update subtask description when focus is lost and save to database
        holder.binding.editTextSubTask.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                subtask.setDescription(holder.binding.editTextSubTask.getText().toString());
                subtaskDAOImpl.updateSubtask(subtask);
            }
        });

        // Remove subtask on button click
        holder.binding.buttonEditSubTask.setOnClickListener(v -> {
            subtaskDAOImpl.deleteSubtask(subtask);
            subtasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, subtasks.size());
        });
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    public void addSubtask(Subtask subtask){
        subtaskDAOImpl.addSubtask(subtask);
        subtasks.add(subtask);
        notifyItemInserted(subtasks.size() - 1);
    }

    public static class SubtaskViewHolder extends RecyclerView.ViewHolder {
        private ItemListSubTaskBinding binding;

        public SubtaskViewHolder(@NonNull ItemListSubTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
