package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListCompletedTaskBinding;
import com.example.todolist.model.CompletedTaskGroup;
import com.example.todolist.model.Task;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {
    private List<CompletedTaskGroup> completedTaskList;
    private Context context;
    private LayoutInflater inflater;
    private TaskDAOImpl taskDAOImpl;

    public CompletedTaskAdapter(List<CompletedTaskGroup> completedTaskList, Context context) {
        this.completedTaskList = completedTaskList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.taskDAOImpl = new TaskDAOImpl(context);
    }

    @NonNull
    @Override
    public CompletedTaskAdapter.CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListCompletedTaskBinding binding = ItemListCompletedTaskBinding.inflate(inflater, parent, false);
        return new CompletedTaskViewHolder(binding, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskAdapter.CompletedTaskViewHolder holder, int position) {
        CompletedTaskGroup group = completedTaskList.get(position);
        holder.binding.textCompletedTime.setText(group.getDate().toString());
        TaskAdapter taskAdapter = new TaskAdapter(context, group.getTasks(), new TaskAdapter.OnTaskInteractionListener() {
            @Override
            public void onTaskStatusChanged() {
            }
        });

        holder.binding.listCompletedTasks.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.listCompletedTasks.setAdapter(taskAdapter);
    }

    @Override
    public int getItemCount() {
        return completedTaskList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        private ItemListCompletedTaskBinding binding;
        public CompletedTaskViewHolder(@NonNull ItemListCompletedTaskBinding binding, int viewType) {
            super(binding.getRoot());
            this.binding = binding;
            binding.timelineCompletedTask.initLine(viewType);
        }
    }
}
