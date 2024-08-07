package com.example.todolist.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.databinding.ItemListManageCategoryBinding;
import com.example.todolist.fragment.EditCategoryDialogFragment;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CategoryManagerAdapter extends RecyclerView.Adapter<CategoryManagerAdapter.CategoryManagerViewHolder> implements EditCategoryDialogFragment.onCategoryEditedListener{

    private List<Category> categories;
    private Context context;
    private LayoutInflater layoutInflater;
    private TaskDAOImpl taskDAOImpl;
    private CategoryDAOImpl categoryDAOImpl;
    private FragmentManager fragmentManager;

    public CategoryManagerAdapter(List<Category> categories, Context context, FragmentManager fragmentManager) {
        this.categories = categories;
        this.context = context;
        this.taskDAOImpl = new TaskDAOImpl(context);
        this.categoryDAOImpl = new CategoryDAOImpl(context);
        this.layoutInflater = LayoutInflater.from(context);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CategoryManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListManageCategoryBinding binding = ItemListManageCategoryBinding.inflate(layoutInflater, parent, false);
        return new CategoryManagerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryManagerViewHolder holder, int position) {
        Category categorySelected = categories.get(position);

        int color = categorySelected.getColor();
        GradientDrawable drawable = (GradientDrawable) holder.binding.categoryColor.getBackground();
        drawable.setColor(color);

        holder.binding.categoryName.setText(categorySelected.getName());

        holder.binding.isVisible.setVisibility(!categorySelected.isVisible() ? View.VISIBLE : View.GONE);

        List<Task> tasks = taskDAOImpl.getTasksByCategoryName(categorySelected.getName());
        int taskCount = tasks.size();
        holder.binding.taskCount.setText(String.valueOf(taskCount));

        holder.binding.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.category_manager_menu, popup.getMenu());

                MenuItem markHide = popup.getMenu().findItem(R.id.hide);
                MenuItem markShow = popup.getMenu().findItem(R.id.show);

                if (categorySelected.isVisible()) {
                    markShow.setVisible(false);
                    markHide.setVisible(true);
                } else {
                    markShow.setVisible(true);
                    markHide.setVisible(false);
                }

                popup.setOnMenuItemClickListener(item -> {

                    if (item.getItemId() == R.id.delete) {
                        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                                .setTitle("Delete the Category?")
                                .setMessage("All tasks in this category will be deleted")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        taskDAOImpl.deleteTasksByCategoryName(categorySelected.getName());
                                        categoryDAOImpl.deleteCategory(categorySelected);
                                        categories.remove(categorySelected);
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
                        EditCategoryDialogFragment editCategoryDialogFragment = EditCategoryDialogFragment.newInstance(categorySelected);
                        editCategoryDialogFragment.setOnCategoryEditedListener(CategoryManagerAdapter.this);
                        editCategoryDialogFragment.show(fragmentManager, "EditCategoryDialogFragment");
                    }

                    if (item.getItemId() == R.id.hide) {
                        categorySelected.setVisible(false);
                        categoryDAOImpl.updateCategory(categorySelected);
                        notifyDataSetChanged();
                    }

                    if (item.getItemId() == R.id.show) {
                        categorySelected.setVisible(true);
                        categoryDAOImpl.updateCategory(categorySelected);
                        notifyDataSetChanged();
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

    @Override
    public void onCategoryEdited(Category category) {
        categoryDAOImpl.updateCategory(category);
        notifyDataSetChanged();
    }

    public static class CategoryManagerViewHolder extends RecyclerView.ViewHolder{
        private ItemListManageCategoryBinding binding;
        public CategoryManagerViewHolder(@NonNull ItemListManageCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
