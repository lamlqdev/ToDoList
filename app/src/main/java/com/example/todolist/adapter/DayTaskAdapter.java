package com.example.todolist.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListDayTaskBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;

import java.util.List;

public class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.DayTaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private LayoutInflater inflater;
    private CategoryDAOImpl categoryDAOImpl;
    private final DayTaskAdapter.OnTaskInteractionListener onTaskInteractionListener;

    public interface OnTaskInteractionListener {
        void onItemTaskClick(Task task);
    }

    public DayTaskAdapter(Context context, List<Task> taskList, DayTaskAdapter.OnTaskInteractionListener onTaskInteractionListener) {
        this.taskList = taskList;
        this.context = context;
        this.onTaskInteractionListener = onTaskInteractionListener;
        this.categoryDAOImpl = new CategoryDAOImpl(context);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DayTaskAdapter.DayTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListDayTaskBinding binding = ItemListDayTaskBinding.inflate(inflater, parent, false);
        return new DayTaskViewHolder(binding, onTaskInteractionListener);
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
        } else {
            View taskIndicator = holder.binding.taskIndicator;
            GradientDrawable background = (GradientDrawable) taskIndicator.getBackground();
            background.setColor(ContextCompat.getColor(context, R.color.blue));
        }

        if (task.getStatus() == 2) {
            String text = task.getTitle();
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.binding.taskText.setText(spannableString);
            holder.binding.overlayView.setVisibility(View.VISIBLE);
        } else {
            holder.binding.overlayView.setVisibility(View.GONE);
        }

        holder.itemView.setTag(task);
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
        public DayTaskViewHolder(@NonNull ItemListDayTaskBinding binding, DayTaskAdapter.OnTaskInteractionListener onTaskInteractionListener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                if (onTaskInteractionListener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Task task = (Task) binding.getRoot().getTag();
                        onTaskInteractionListener.onItemTaskClick(task);
                    }
                }
            });
        }
    }
}
