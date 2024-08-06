package com.example.todolist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListManageCategoryBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CategoryManagerAdapter extends RecyclerView.Adapter<CategoryManagerAdapter.CategoryManagerViewHolder>{

    private List<Category> categories;
    private Context context;
    private LayoutInflater layoutInflater;
    private TaskDAOImpl taskDAOImpl;
    private CategoryDAOImpl categoryDAOImpl;

    public CategoryManagerAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
        this.taskDAOImpl = new TaskDAOImpl(context);
        this.categoryDAOImpl = new CategoryDAOImpl(context);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CategoryManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListManageCategoryBinding binding = ItemListManageCategoryBinding.inflate(layoutInflater, parent, false);
        return new CategoryManagerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryManagerViewHolder holder, int position) {
        Category category = categories.get(position);

        int color = category.getColor();
        GradientDrawable drawable = (GradientDrawable) holder.binding.categoryColor.getBackground();
        drawable.setColor(color);

        holder.binding.categoryName.setText(category.getName());

        List<Task> tasks = taskDAOImpl.getTasksByCategoryName(category.getName());
        int taskCount = tasks.size();
        holder.binding.taskCount.setText(String.valueOf(taskCount));

        holder.binding.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.category_manager_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {

                    if (item.getItemId() == R.id.delete) {
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                                .setTitle("Delete the Category?")
                                .setMessage("All tasks in this category will be deleted")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        taskDAOImpl.deleteTasksByCategoryName(category.getName());
                                        categoryDAOImpl.deleteCategory(category);
                                        categories.remove(category);
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                    if (item.getItemId() == R.id.edit) {

                    }

                    return true;
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryManagerViewHolder extends RecyclerView.ViewHolder{
        private ItemListManageCategoryBinding binding;
        public CategoryManagerViewHolder(@NonNull ItemListManageCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
