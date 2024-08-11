package com.example.todolist.fragment;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.DAO.CategoryDAOImpl;
import com.example.todolist.DAO.TaskDAOImpl;
import com.example.todolist.R;
import com.example.todolist.adapter.Next7DayTaskAdapter;
import com.example.todolist.databinding.FragmentMineBinding;
import com.example.todolist.model.Category;
import com.example.todolist.model.Task;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MineFragment extends Fragment {
    private FragmentMineBinding binding;
    private TaskDAOImpl taskDAOImpl;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Task> next7DayTaskList;
    private Next7DayTaskAdapter next7DayTaskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        initializeData();
        setWidget();
        setBarChart();
        setPieChart();
        setEvents();
        return binding.getRoot();
    }

    private void setPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<Category> categories = categoryDAOImpl.getAllVisibleCategories();
        List<LegendEntry> legendEntries = new ArrayList<>();

        Category noCategory = new Category(-1, "No Category", ContextCompat.getColor(getContext(), R.color.blue), true);
        categories.add(noCategory);

        for (Category category : categories) {
            int pendingTasks = taskDAOImpl.getNumberPendingTaskByCategory(category.getCategoryID());
            if (pendingTasks > 0) {
                pieEntries.add(new PieEntry(pendingTasks, category.getName()));
                colors.add(category.getColor());

                LegendEntry legendEntry = new LegendEntry();
                legendEntry.label = category.getName() + " [" + pendingTasks + "]";
                legendEntry.formColor = category.getColor();
                legendEntries.add(legendEntry);
            }
        }

        if (pieEntries.isEmpty()) {
            binding.pieChart.setNoDataText("No pending tasks");
            return;
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(12f); // Set the size of the text inside the pie chart
        pieDataSet.setSliceSpace(3f); // Space between slices
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);

        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(45f);
        binding.pieChart.setTransparentCircleRadius(50f);

        // Configure legend
        Legend legend = binding.pieChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setDrawInside(false);
        legend.setTextSize(14f); // Set the size of the text in the legend
        legend.setXOffset(10f); // Offset from the chart
        legend.setYOffset(0f); // Offset from the chart

        legend.setCustom(legendEntries);

        binding.pieChart.invalidate();
    }

    private void setBarChart() {
        List<String> daysOfWeek = new ArrayList<>();
        List<BarEntry> barEntries = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");

        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            daysOfWeek.add(day.format(formatter));
            int completedTasks = taskDAOImpl.getNumberCompletedTaskByDate(day);
            barEntries.add(new BarEntry(6 - i, completedTasks));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Completed Tasks");
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        BarData barData = new BarData(dataSet);

        binding.barChart.setData(barData);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart.getAxisLeft().setGranularity(1f);
        binding.barChart.getAxisRight().setEnabled(false);
        binding.barChart.setFitBars(true);

        // Điều chỉnh kích thước chữ của trục X và trục Y
        binding.barChart.getXAxis().setTextSize(10f); // Kích thước chữ cho trục X
        binding.barChart.getAxisLeft().setTextSize(10f); // Kích thước chữ cho trục Y

        // Điều chỉnh độ rộng của đường kẻ trục
        binding.barChart.getXAxis().setAxisLineWidth(0.5f); // Độ rộng đường kẻ trục X
        binding.barChart.getAxisLeft().setAxisLineWidth(0.5f); // Độ rộng đường kẻ trục Y

        // Đặt kiểu chữ cho trục X và trục Y
        Typeface customTypeface = ResourcesCompat.getFont(getContext(), R.font.inter_regular);
        binding.barChart.getXAxis().setTypeface(customTypeface); // Đặt kiểu chữ cho trục X
        binding.barChart.getAxisLeft().setTypeface(customTypeface); // Đặt kiểu chữ cho trục Y


        // Ẩn các hàng ngang và hàng dọc
        binding.barChart.getXAxis().setDrawGridLines(false);
        binding.barChart.getAxisLeft().setDrawGridLines(false);

        // Ẩn nhãn và số liệu trên cột
        dataSet.setDrawValues(false);
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.getDescription().setEnabled(false);

        binding.barChart.getAxisLeft().setSpaceTop(0f);
        binding.barChart.getAxisLeft().setSpaceBottom(0f);

        // Vô hiệu hóa tất cả các chức năng tương tác
        binding.barChart.setTouchEnabled(false);
        binding.barChart.setDragEnabled(false);
        binding.barChart.setScaleEnabled(false);
        binding.barChart.setPinchZoom(false);
        binding.barChart.setHighlightPerTapEnabled(false);
        binding.barChart.setHighlightPerDragEnabled(false);

        binding.barChart.invalidate();
    }


    private void setWidget() {
        List<Task> completedTasks = taskDAOImpl.getAllCompletedTasks();
        List<Task> allTasks = taskDAOImpl.getAllTasks();
        int numberCompletedTask = completedTasks.size();
        int numberPendingTask = allTasks.size() - numberCompletedTask;

        binding.numberCompletedTask.setText(String.valueOf(numberCompletedTask));
        binding.numberPendingTask.setText(String.valueOf(numberPendingTask));

        next7DayTaskList = taskDAOImpl.getTasksForNext7Days();
        next7DayTaskAdapter = new Next7DayTaskAdapter(getContext(), next7DayTaskList);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewTasks.setAdapter(next7DayTaskAdapter);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        String todayDate = LocalDate.now().format(formatter);
        String startWeekDate = LocalDate.now().minusDays(6).format(formatter);
        binding.textEndWeek.setText(todayDate);
        binding.textStartWeek.setText(startWeekDate);

        Set<LocalDate> activeDays = new HashSet<>();
        for (Task task : allTasks) {
            if (task.getCreatedAt() != null) {
                LocalDate taskDate = LocalDate.from(task.getCreatedAt());
                activeDays.add(taskDate);
            }
        }
        int activeDaysCount = activeDays.size();
        String message = getString(R.string.keep_plan_for_x_days, activeDaysCount);
        binding.countDayText.setText(message);
    }

    private void setEvents() {

    }

    private void initializeData() {
        taskDAOImpl = new TaskDAOImpl(getContext());
        categoryDAOImpl = new CategoryDAOImpl(getContext());
    }
}