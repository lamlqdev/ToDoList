package com.example.todolist.customview;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;

public class MaxHeightRecyclerView extends RecyclerView {
    private int maxHeight;

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        maxHeight = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView)
                .getDimensionPixelSize(R.styleable.MaxHeightRecyclerView_maxHeight, 200);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int heightMeasureSpec;
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        } else {
            heightMeasureSpec = heightSpec;
        }
        super.onMeasure(widthSpec, heightMeasureSpec);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }
}
