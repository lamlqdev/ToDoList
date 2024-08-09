package com.example.todolist.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.activity.UpdateTaskActivity;
import com.example.todolist.adapter.DayTaskAdapter;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;

public class CalendarFragment extends Fragment implements BottomSheetAddTaskFragment.OnTaskAddedListener, DayTaskAdapter.OnTaskInteractionListener {
    private FragmentCalendarBinding binding;
    private CalendarView calendarView;
    private LocalDate selectedDate = LocalDate.now();;
    private LocalDate mCurrentMonth = YearMonth.now().atDay(1);
    private DayTaskAdapter dayTaskAdapter;
    private TaskDAOImpl taskDAOImpl;
    private CategoryDAOImpl categoryDAOImpl;
    private ActivityResultLauncher<Intent> updateTaskLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);

        this.taskDAOImpl = new TaskDAOImpl(requireContext());
        this.categoryDAOImpl = new CategoryDAOImpl(requireContext());

        calendarView = binding.calendarView;
        dayTaskAdapter = new DayTaskAdapter(requireContext(), List.of(), this);
        binding.recyclerViewListTodayTask.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewListTodayTask.setAdapter(dayTaskAdapter);

        updateTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == UpdateTaskActivity.RESULT_OK) {
                    loadTasksForDate(selectedDate);
                }
            }
        );

        setupCalendar();
        setWidgets();
        setEvents();

        calendarView.notifyDateChanged(selectedDate);
        loadTasksForDate(selectedDate);

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
                List<Task> dayTasks = taskDAOImpl.getTasksByDueDate(calendarDay.getDate());
                Iterator<Task> iterator = dayTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getCategoryID() != -1) {
                        Category category = categoryDAOImpl.getCategory(task.getCategoryID());
                        if (category != null && !category.isVisible()) {
                            iterator.remove();
                        }
                    }
                }

                // Xử lý hiển thị ngày trong tháng
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.calendarDayText.setTextColor(getResources().getColor(R.color.black, getResources().newTheme()));
                } else {
                    container.calendarDayText.setTextColor(getResources().getColor(R.color.grey_text, getResources().newTheme()));
                }

                container.day = calendarDay;
                TextView textView = container.calendarDayText;
                textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                View dotView1 = container.dotView1;
                View dotView2 = container.dotView2;
                View dotView3 = container.dotView3;
                LinearLayout dotContainer = container.dotContainer;

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    if (dayTasks.isEmpty()){
                        dotContainer.setVisibility(View.GONE);
                    } else {
                        dotContainer.setVisibility(View.VISIBLE);
                        List<Integer> colors = categoryDAOImpl.getCategoryColorFromTasks(dayTasks);
                        if (colors.size() == 1) {
                            dotView1.setVisibility(View.VISIBLE);
                            GradientDrawable drawable = (GradientDrawable) container.dotView1.getBackground();
                            drawable.setColor(colors.get(0));
                        }
                        if (colors.size() == 2) {
                            dotView1.setVisibility(View.VISIBLE);
                            GradientDrawable drawable1 = (GradientDrawable) container.dotView1.getBackground();
                            drawable1.setColor(colors.get(0));
                            dotView2.setVisibility(View.VISIBLE);
                            GradientDrawable drawable2 = (GradientDrawable) container.dotView2.getBackground();
                            drawable2.setColor(colors.get(1));
                        }
                        if (colors.size() == 3) {
                            dotView1.setVisibility(View.VISIBLE);
                            GradientDrawable drawable1 = (GradientDrawable) container.dotView1.getBackground();
                            drawable1.setColor(colors.get(0));
                            dotView2.setVisibility(View.VISIBLE);
                            GradientDrawable drawable2 = (GradientDrawable) container.dotView2.getBackground();
                            drawable2.setColor(colors.get(1));
                            dotView3.setVisibility(View.VISIBLE);
                            GradientDrawable drawable3 = (GradientDrawable) container.dotView3.getBackground();
                            drawable3.setColor(colors.get(2));
                        }
                    }

                    textView.setVisibility(View.VISIBLE);

                    if (calendarDay.getDate().equals(selectedDate)) {
                        // Nếu là ngày được chọn, thay đổi màu chữ và nền.
                        textView.setTextColor(Color.WHITE);
                        textView.setBackgroundResource(R.drawable.background_selection_day);
                        dotContainer.setVisibility(View.GONE);

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

        binding.floatingAddButton.setOnClickListener(v -> {
            BottomSheetAddTaskFragment bottomSheet = BottomSheetAddTaskFragment.newInstance(selectedDate);
            bottomSheet.setOnTaskAddedListener(CalendarFragment.this);
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
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

    @Override
    public void onItemTaskClick(Task task) {
        Intent intent = new Intent(requireContext(), UpdateTaskActivity.class);
        intent.putExtra("task", task);
        updateTaskLauncher.launch(intent);
    }

    @Override
    public void onTaskAdded(Task newTask) {
        loadTasksForDate(selectedDate);
    }

    public class DayViewContainer extends ViewContainer {
        private CalendarDay day;
        public final TextView calendarDayText;
        public final View dotView1;
        public final View dotView2;
        public final View dotView3;
        public final LinearLayout dotContainer;

        public DayViewContainer(@NonNull View view) {
            super(view);
            calendarDayText = view.findViewById(R.id.calendarDayText);
            dotView1 = view.findViewById(R.id.dotView1);
            dotView2 = view.findViewById(R.id.dotView2);
            dotView3 = view.findViewById(R.id.dotView3);
            dotContainer = view.findViewById(R.id.dotContainer);

            view.setOnClickListener(v -> {
                if (day.getPosition() == DayPosition.MonthDate) {
                    if (!day.getDate().equals(selectedDate)) {
                        LocalDate previousSelectedDate = selectedDate;
                        selectedDate = day.getDate();
                        if (previousSelectedDate != null) {
                            calendarView.notifyDateChanged(previousSelectedDate); // Cập nhật ngày trước đó
                        }
                        calendarView.notifyDateChanged(day.getDate()); // Cập nhật ngày mới được chọn
                        loadTasksForDate(selectedDate);
                    }
                }
            });
        }
    }

    private void loadTasksForDate(LocalDate date) {
        if (date != null) {
            List<Task> tasks = taskDAOImpl.getTasksByDueDate(date);
            Iterator<Task> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getCategoryID() != -1) {
                    Category category = categoryDAOImpl.getCategory(task.getCategoryID());
                    if (category != null && !category.isVisible()) {
                        iterator.remove();
                    }
                }
            }

            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task task1, Task task2) {
                    if (task1.getStatus() != 2 && task2.getStatus() == 2) {
                        return -1;
                    }
                    else if (task1.getStatus() == 2 && task2.getStatus() != 2) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });

            if (tasks.isEmpty()) {
                binding.recyclerViewListTodayTask.setVisibility(View.GONE);
                binding.emptyViewContainer.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewListTodayTask.setVisibility(View.VISIBLE);
                binding.emptyViewContainer.setVisibility(View.GONE);
            }
            dayTaskAdapter.updateTaskList(tasks);
        } else {
            dayTaskAdapter.updateTaskList(List.of());
            binding.recyclerViewListTodayTask.setVisibility(View.GONE);
            binding.emptyViewContainer.setVisibility(View.VISIBLE);
        }
    }
}