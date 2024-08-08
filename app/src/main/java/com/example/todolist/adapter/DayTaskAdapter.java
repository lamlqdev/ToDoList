package com.example.todolist.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.databinding.ItemListDayTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;

import java.util.List;

public class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.DayTaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private LayoutInflater inflater;
    private CategoryDAOImpl categoryDAOImpl;

    public DayTaskAdapter(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.context = context;
        this.categoryDAOImpl = new CategoryDAOImpl(context);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DayTaskAdapter.DayTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListDayTaskBinding binding = ItemListDayTaskBinding.inflate(inflater, parent, false);
        return new DayTaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayTaskAdapter.DayTaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.binding.taskText.setText(task.getTitle());
        Category category = categoryDAOImpl.getCategory(task.getCategoryID());

        if (category != null) {
            View taskIndicator = holder.binding.taskIndicator;
            GradientDrawable background = (GradientDrawable) taskIndicator.getBackground();
            background.setColor(category.getColor());
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTaskList(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    public static class DayTaskViewHolder extends RecyclerView.ViewHolder{
        private ItemListDayTaskBinding binding;
        public DayTaskViewHolder(@NonNull ItemListDayTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
