package com.example.todolist.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.todolist.R;
import com.example.todolist.databinding.FragmentDialogDateBinding;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;

public class DatePickerDialogFragment extends DialogFragment {
    private FragmentDialogDateBinding binding;
    private CalendarView calendarView;
    private LocalDate selectedDate = null;
    LocalDate mCurrentMonth = YearMonth.now().atDay(1);
    private OnDateSelectedListener dateSelectedListener;
    public static final String ARG_DATE_SELECTED = "date_selected";

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public static DatePickerDialogFragment newInstance(LocalDate selectedDate) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE_SELECTED, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDialogDateBinding.inflate(inflater, container, false);
        calendarView = binding.calendarView;
        setShowsDialog(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setWidgets();
        setEvents();

        if (getArguments() != null) {
            selectedDate = (LocalDate) getArguments().getSerializable(ARG_DATE_SELECTED);
            if (selectedDate != null) {
                calendarView.notifyDateChanged(selectedDate);
            } else {
                selectedDate = LocalDate.now();
                calendarView.notifyDateChanged(selectedDate);
            }
            updateButtonStates(selectedDate);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int widthInPixels = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int heightInPixels = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(widthInPixels, heightInPixels);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setWidgets() {
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.calendarDayText.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

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

        updateCalendar(mCurrentMonth);
    }

    private void setEvents() {
        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonDone.setOnClickListener(v -> dismiss());

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

        binding.buttonNoDate.setOnClickListener(v -> {
            updateButtonState(binding.buttonNoDate);
            if (selectedDate != null) {
                LocalDate previousSelection = selectedDate;
                selectedDate = null;
                calendarView.notifyDateChanged(previousSelection); // Cập nhật lại ngày đã chọn trước đó
            }
            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(selectedDate);
            }
        });

        binding.buttonToday.setOnClickListener(v -> handleDateSelection(LocalDate.now(), binding.buttonToday));

        binding.buttonTomorrow.setOnClickListener(v -> handleDateSelection(LocalDate.now().plusDays(1), binding.buttonTomorrow));

        binding.button3DaysLater.setOnClickListener(v -> handleDateSelection(LocalDate.now().plusDays(3), binding.button3DaysLater));

        binding.buttonThisSunday.setOnClickListener(v -> handleDateSelection(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), binding.buttonThisSunday));
    }

    private void handleDateSelection(LocalDate date, Button button) {
        calendarView.scrollToMonth(YearMonth.from(date));
        updateButtonState(button);
        if (!date.equals(selectedDate)) {
            LocalDate previousSelection = selectedDate;
            selectedDate = date;
            calendarView.notifyDateChanged(date);
            if (previousSelection != null) {
                calendarView.notifyDateChanged(previousSelection);
            }
        } else {
            calendarView.notifyDateChanged(date);
        }
        if (dateSelectedListener != null) {
            dateSelectedListener.onDateSelected(selectedDate);
        }
    }

    private void updateButtonState(Button selectedButton) {
        for (Button button : new Button[]{binding.buttonNoDate, binding.buttonToday, binding.buttonTomorrow, binding.buttonThisSunday, binding.button3DaysLater}) {
            button.setBackgroundResource(R.drawable.background_white_radius_5dp);
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.task_title_bg)));
            button.setTextColor(Color.BLACK);
        }
        // Thay đổi màu nền và màu chữ của button được chọn
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        selectedButton.setTextColor(Color.WHITE);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    public class DayViewContainer extends ViewContainer {
        private CalendarDay day;
        public final TextView calendarDayText;

        public DayViewContainer(View view) {
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
                    updateButtonStates(selectedDate);

                    // Gọi callback để truyền ngày được chọn về listener
                    if (dateSelectedListener != null) {
                        dateSelectedListener.onDateSelected(selectedDate);
                    }
                }
            });
        }
    }

    private void updateButtonStates(LocalDate selectedDate) {
        for (Button button : new Button[]{binding.buttonNoDate, binding.buttonToday, binding.buttonTomorrow, binding.buttonThisSunday, binding.button3DaysLater}) {
            button.setBackgroundResource(R.drawable.background_white_radius_5dp);
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.task_title_bg)));
            button.setTextColor(Color.BLACK);
        }

        if (selectedDate != null) {
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate threeDaysLater = today.plusDays(3);
            LocalDate thisSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            if (selectedDate.equals(today)) {
                updateButtonState(binding.buttonToday);
            } else if (selectedDate.equals(tomorrow)) {
                updateButtonState(binding.buttonTomorrow);
            } else if (selectedDate.equals(threeDaysLater)) {
                updateButtonState(binding.button3DaysLater);
            } else if (selectedDate.equals(thisSunday)) {
                updateButtonState(binding.buttonThisSunday);
            }
        }
    }
}
