package com.example.todolist.adapter;

import android.content.Context;
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

    public TaskAdapter(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.context = context;
        this.taskDAOImpl = new TaskDAOImpl(context);
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListTaskBinding binding = ItemListTaskBinding.inflate(inflater, parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.binding.taskText.setText(task.getTitle());
        holder.binding.taskCheckBox.setChecked(task.getStatus() == 2);

        holder.binding.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setStatus(isChecked ? 2 : 1);
            taskDAOImpl.updateTask(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        private ItemListTaskBinding binding;
        public TaskViewHolder(@NonNull ItemListTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
