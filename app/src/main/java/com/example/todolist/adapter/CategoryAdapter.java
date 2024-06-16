package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.model.Category;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private Context context;
    private LayoutInflater layoutInflater;
    private int selectedItem = -1; // Vị trí của button được chọn, mặc định là -1


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
        View view = layoutInflater.inflate(R.layout.item_list_button_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.button.setText(category.getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = position; // Cập nhật vị trí của button được chọn
                notifyDataSetChanged(); // Thông báo cho RecyclerView về sự thay đổi để cập nhật giao diện
            }
        });
        if(position == selectedItem) {
            holder.button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blue2));
            holder.button.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey2));
            holder.button.setTextColor(ContextCompat.getColor(context, R.color.grey_text));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton button;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.category_button);
        }
    }
}

