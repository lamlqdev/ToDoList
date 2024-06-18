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

import com.example.todolist.R;
import com.example.todolist.model.Category;
import com.example.todolist.model.Subtask;

import java.util.List;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder> {
    private List<Subtask> subtasks;
    private Context context;
    private LayoutInflater layoutInflater;

    public SubtaskAdapter(Context context, List<Subtask> subtasks) {
        this.subtasks = subtasks;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SubtaskAdapter.SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list_sub_task, parent, false);
        return new SubtaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskAdapter.SubtaskViewHolder holder, int position) {
        Subtask subtask = subtasks.get(position);
        holder.checkboxIsDone.setChecked(subtask.getStatus() == 2);
        holder.editTextSubTask.setText(subtask.getDescription());
        holder.editTextSubTask.requestFocus();

        holder.checkboxIsDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subtask.setStatus(isChecked ? 2 : 1);
        });

        holder.editTextSubTask.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                subtask.setDescription(holder.editTextSubTask.getText().toString());
            }
        });

        holder.buttonEditSubTask.setOnClickListener(v -> {
            subtasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, subtasks.size());
        });
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    public static class SubtaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxIsDone;
        private EditText editTextSubTask;
        private ImageButton buttonEditSubTask;
        public SubtaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxIsDone = itemView.findViewById(R.id.checkboxIsDone);
            editTextSubTask = itemView.findViewById(R.id.editTextSubTask);
            buttonEditSubTask = itemView.findViewById(R.id.buttonEditSubTask);
        }
    }
}
