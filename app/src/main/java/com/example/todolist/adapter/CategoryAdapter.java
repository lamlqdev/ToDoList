package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.databinding.ItemListButtonCategoryBinding;
import com.example.todolist.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private Context context;
    private LayoutInflater layoutInflater;
    private int selectedPosition = RecyclerView.NO_POSITION;;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.categories = categories;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        Category allCategory = new Category();
        allCategory.setName(context.getString(R.string.all));
        categories.add(0, allCategory);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListButtonCategoryBinding binding = ItemListButtonCategoryBinding.inflate(layoutInflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.categoryButton.setText(category.getName());

        holder.binding.categoryButton.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            notifyItemChanged(selectedPosition);
            notifyItemChanged(previousPosition);
        });

        if (position == selectedPosition) {
            holder.binding.categoryButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blue2));
            holder.binding.categoryButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.binding.categoryButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey2));
            holder.binding.categoryButton.setTextColor(ContextCompat.getColor(context, R.color.grey_text));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setSelectedItem(int position) {
        selectedPosition = position;
        notifyItemChanged(selectedPosition);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemListButtonCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemListButtonCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
