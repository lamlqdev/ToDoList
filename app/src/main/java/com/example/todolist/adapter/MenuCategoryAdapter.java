package com.example.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.R;
import com.example.todolist.model.Category;

import java.util.List;

public class MenuCategoryAdapter extends ArrayAdapter<Category> {
    public MenuCategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
    }

    private static class ViewHolder {
        TextView tvCategory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_selected, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCategory = convertView.findViewById(R.id.categorySelected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = getItem(position);
        if (category != null) {
            viewHolder.tvCategory.setText(category.getName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_textview_category, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvCategory = convertView.findViewById(R.id.categoryName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = getItem(position);
        if (category != null) {
            viewHolder.tvCategory.setText(category.getName());
        }

        return convertView;
    }
}
