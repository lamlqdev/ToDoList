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

import com.example.todolist.R;
import com.example.todolist.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    private List<Task> taskList;
    private LayoutInflater inflater;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_list_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskText.setText(task.getTitle());
        holder.taskCheckBox.setChecked(task.getStatus() == 2);
        holder.taskCheckBox.setOnClickListener(v -> {
            task.setStatus(task.getStatus() == 2 ? 1 : 2);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        private CheckBox taskCheckBox;
        private TextView taskText;
        private ImageView flagIcon;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskText = itemView.findViewById(R.id.taskText);
            flagIcon = itemView.findViewById(R.id.flagIcon);
        }
    }
}
