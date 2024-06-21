package com.example.todolist.adapter;

import android.view.View;
import android.widget.TextView;

import com.example.todolist.R;
import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer extends ViewContainer {

    public final TextView calendarDayText;

    public DayViewContainer(View view) {
        super(view);
        calendarDayText = view.findViewById(R.id.calendarDayText);
    }

}
