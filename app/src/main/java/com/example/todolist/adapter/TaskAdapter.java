package com.example.todolist.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListTaskBinding;
import com.example.todolist.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    private List<Task> taskList;
    private Context context;
    private LayoutInflater inflater;
    private TaskDAOImpl taskDAOImpl;
    private final OnTaskInteractionListener onTaskInteractionListener;

    public interface OnTaskInteractionListener {
        void onTaskStatusChanged();
        void onItemTaskClick(int position);
    }

    public TaskAdapter(Context context, List<Task> taskList, OnTaskInteractionListener onTaskInteractionListener) {
        this.taskList = taskList;
        this.context = context;
        this.onTaskInteractionListener = onTaskInteractionListener;
        this.taskDAOImpl = new TaskDAOImpl(context);
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListTaskBinding binding = ItemListTaskBinding.inflate(inflater, parent, false);
        return new TaskViewHolder(binding, onTaskInteractionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task updateTask = taskList.get(position);

        if (updateTask.getStatus() == 2) {
            String text = updateTask.getTitle();
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.binding.taskText.setText(spannableString);
        } else{
            holder.binding.taskText.setText(updateTask.getTitle());
        }

        holder.binding.taskCheckBox.setChecked(updateTask.getStatus() == 2);
        holder.binding.taskCheckBox.setOnCheckedChangeListener(null);
        holder.binding.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateTask.setStatus(isChecked ? 2 : 1);
            taskDAOImpl.updateTaskStatus(updateTask);

            if (onTaskInteractionListener != null) {
                onTaskInteractionListener.onTaskStatusChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        private final ItemListTaskBinding binding;
        public TaskViewHolder(@NonNull ItemListTaskBinding binding, OnTaskInteractionListener onTaskInteractionListener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                if (onTaskInteractionListener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onTaskInteractionListener.onItemTaskClick(pos);
                    }
                }
            });
        }
    }
}
