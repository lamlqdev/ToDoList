package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.ItemListNext7DayTaskBinding;
import com.example.todolist.model.Task;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Next7DayTaskAdapter extends RecyclerView.Adapter<Next7DayTaskAdapter.Next7DayTaskViewHolder> {

    private List<Task> taskList;
    private Context context;
    private LayoutInflater inflater;

    public Next7DayTaskAdapter(Context context, List<Task> taskList){
        this.taskList = taskList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Next7DayTaskAdapter.Next7DayTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListNext7DayTaskBinding binding = ItemListNext7DayTaskBinding.inflate(inflater, parent, false);
        return new Next7DayTaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Next7DayTaskAdapter.Next7DayTaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.binding.nameTaskText.setText(task.getTitle());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        String formattedDate = task.getDueDate().format(formatter);
        holder.binding.dateTaskText.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class Next7DayTaskViewHolder extends RecyclerView.ViewHolder{

        private ItemListNext7DayTaskBinding binding;

        public Next7DayTaskViewHolder (@NonNull ItemListNext7DayTaskBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
