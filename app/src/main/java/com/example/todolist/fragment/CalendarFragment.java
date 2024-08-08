package com.example.todolist.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.todolist.R;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.example.todolist.databinding.FragmentCalendarBinding;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    private CalendarView calendarView;
    private LocalDate selectedDate = null;
    LocalDate mCurrentMonth = YearMonth.now().atDay(1);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        calendarView = binding.calendarView;
        setupCalendar();
        setWidgets();
        setEvents();
        return binding.getRoot();
    }

    private void setupCalendar() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(10);
        YearMonth endMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = firstDayOfWeekFromLocale();
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);
    }

    private void setWidgets() {
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.calendarDayText.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                // Xử lý hiển thị ngày trong tháng
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.calendarDayText.setTextColor(getResources().getColor(R.color.black, getResources().newTheme()));
                } else {
                    container.calendarDayText.setTextColor(getResources().getColor(R.color.grey_text, getResources().newTheme()));
                }

                container.day = calendarDay;
                TextView textView = container.calendarDayText;
                textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    textView.setVisibility(View.VISIBLE);
                    if (calendarDay.getDate().equals(selectedDate)) {
                        // Nếu là ngày được chọn, thay đổi màu chữ và nền.
                        textView.setTextColor(Color.WHITE);
                        textView.setBackgroundResource(R.drawable.background_selection_day);
                    } else {
                        // Nếu không phải ngày được chọn, đặt lại màu chữ và nền.
                        textView.setTextColor(Color.BLACK);
                        textView.setBackground(null);
                    }
                } else {
                    textView.setVisibility(View.VISIBLE);
                    Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_regular);
                    textView.setTypeface(typeface);
                }
            }
        });

        ViewGroup titlesContainer = binding.titlesContainer.getRoot();
        List<DayOfWeek> daysOfWeek = daysOfWeek(firstDayOfWeekFromLocale());
        for (int index = 0; index < titlesContainer.getChildCount(); index++) {
            TextView textView = (TextView) titlesContainer.getChildAt(index);
            DayOfWeek dayOfWeek = daysOfWeek.get(index);
            String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
            textView.setText(title);
        }
    }

    private void setEvents() {
        binding.buttonPreMonth.setOnClickListener(v -> {
            mCurrentMonth = mCurrentMonth.minusMonths(1);
            updateCalendar(mCurrentMonth);
        });

        binding.buttonNextMonth.setOnClickListener(v -> {
            mCurrentMonth = mCurrentMonth.plusMonths(1);
            updateCalendar(mCurrentMonth);
        });

        binding.calendarView.setMonthScrollListener(calendarMonth -> {
            mCurrentMonth = calendarMonth.getYearMonth().atDay(1);
            updateMonthYearDisplay();
            return Unit.INSTANCE;
        });
    }

    private void updateCalendar(LocalDate currentMonth) {
        YearMonth yearMonth = YearMonth.from(currentMonth);
        calendarView.setup(yearMonth.minusMonths(100), yearMonth.plusMonths(100), firstDayOfWeekFromLocale());
        calendarView.scrollToMonth(yearMonth);
        updateMonthYearDisplay();
    }

    private void updateMonthYearDisplay() {
        YearMonth yearMonth = YearMonth.from(mCurrentMonth);
        binding.textViewMonth.setText(mCurrentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        binding.textViewYear.setText(String.valueOf(yearMonth.getYear()));
    }

    public class DayViewContainer extends ViewContainer {
        private CalendarDay day;
        public final TextView calendarDayText;

        public DayViewContainer(@NonNull View view) {
            super(view);
            calendarDayText = view.findViewById(R.id.calendarDayText);

            view.setOnClickListener(v -> {
                if (day.getPosition() == DayPosition.MonthDate) {
                    LocalDate currentSelection = selectedDate;
                    if (currentSelection == day.getDate()) {
                        selectedDate = null; // Bỏ chọn ngày nếu ngày đã được chọn trước đó.
                        calendarView.notifyDateChanged(currentSelection); // Cập nhật lại giao diện cho ngày bị bỏ chọn.
                    } else {
                        selectedDate = day.getDate(); // Chọn ngày mới.
                        calendarView.notifyDateChanged(day.getDate()); // Cập nhật lại giao diện cho ngày được chọn.
                        if (currentSelection != null) {
                            calendarView.notifyDateChanged(currentSelection); // Bỏ chọn ngày trước đó nếu có.
                        }
                    }
                }
            });
        }
    }
}