package com.example.todolist.fragment;

import static androidx.credentials.ClearCredentialRequestTypes.CLEAR_CREDENTIAL_STATE;

import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.credentials.ClearCredentialRequestTypes;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.bumptech.glide.Glide;
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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

public class MineFragment extends Fragment {
    private FragmentMineBinding binding;
    private TaskDAOImpl taskDAOImpl;
    private CategoryDAOImpl categoryDAOImpl;
    private List<Task> next7DayTaskList;
    private Next7DayTaskAdapter next7DayTaskAdapter;
    private static final String CLIENT_ID = "406348576873-p2uvdliji2jtc4qbooqq8ieb0kq6c05m.apps.googleusercontent.com";
    private CredentialManager credentialManager;
    private boolean isLoginSuccess = false;
    private boolean firstLogin = false;
    private int activeDaysCount;
    private String nameUser;
    private String pictureUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        credentialManager = CredentialManager.create(getContext());
        initializeData();
        setWidget();
        setBarChart();
        setPieChart();
        setEvents();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        isLoginSuccess = sharedPreferences.getBoolean("isLoginSuccess", false);
        if (isLoginSuccess) {
            nameUser = sharedPreferences.getString("nameUser", "");
            pictureUrl = sharedPreferences.getString("pictureUrl", "");

            binding.clickToLoginText.setVisibility(View.GONE);
            binding.countDayText.setVisibility(View.GONE);
            binding.countDayTextLogin.setVisibility(View.VISIBLE);
            binding.nameTextLogin.setVisibility(View.VISIBLE);
            binding.buttonLogout.setVisibility(View.VISIBLE);

            binding.nameTextLogin.setText(nameUser);

            String message = getString(R.string.keep_plan_for_x_days, activeDaysCount);
            binding.countDayTextLogin.setText(message);

            Glide.with(requireContext())
                    .load(pictureUrl)
                    .into(binding.imageViewAvatar);
        }

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
        activeDaysCount = activeDays.size();
        String message = getString(R.string.keep_plan_for_x_days, activeDaysCount);
        binding.countDayText.setText(message);
    }

    private void setEvents() {
        binding.clickToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setTitle("Log out")
                        .setMessage("Are you sure want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleLogout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void signIn() {
        String nonce = generateNonce();
        CancellationSignal cancellationSignal = new CancellationSignal();
        Executor executor = Executors.newSingleThreadExecutor();
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(CLIENT_ID)
                .setAutoSelectEnabled(true)
                .setNonce(nonce)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireActivity(),
                request,
                cancellationSignal ,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        handleFailure(e);
                    }
                });
    }

    private void handleFailure(GetCredentialException e) {
        Log.e("MineFragment", "Failed to get credential: " + e.getMessage(),e);
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), "Failed to get credential: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void handleSignIn(GetCredentialResponse getCredentialResponse) {
        Credential credential = getCredentialResponse.getCredential();

        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                String idToken = googleIdTokenCredential.getIdToken();
                isLoginSuccess = true;
                firstLogin = true;
                try {
                    JWT jwt = new JWT(idToken);
                    nameUser = jwt.getClaim("name").asString();  // Tên của người dùng
                    pictureUrl = jwt.getClaim("picture").asString(); // URL hình ảnh của người dùng

                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nameUser", nameUser);
                    editor.putString("pictureUrl", pictureUrl);
                    editor.putString("idToken", idToken);
                    editor.putBoolean("isLoginSuccess", true);
                    editor.apply();

                } catch (Exception e) {
                    Log.e("MineFragment", "Failed to decode ID token: " + e.getMessage(), e);
                }
            }
        }
    }

    private void handleLogout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        isLoginSuccess = false;

        binding.clickToLoginText.setVisibility(View.VISIBLE);
        binding.countDayText.setVisibility(View.VISIBLE);
        binding.countDayTextLogin.setVisibility(View.GONE);
        binding.nameTextLogin.setVisibility(View.GONE);
        binding.buttonLogout.setVisibility(View.GONE);
        binding.imageViewAvatar.setImageResource(R.drawable.none_user);
    }

    private void initializeData() {
        taskDAOImpl = new TaskDAOImpl(getContext());
        categoryDAOImpl = new CategoryDAOImpl(getContext());
    }

    public String generateNonce() {
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstLogin) {
            binding.clickToLoginText.setVisibility(View.GONE);
            binding.countDayText.setVisibility(View.GONE);
            binding.countDayTextLogin.setVisibility(View.VISIBLE);
            binding.nameTextLogin.setVisibility(View.VISIBLE);
            binding.buttonLogout.setVisibility(View.VISIBLE);

            binding.nameTextLogin.setText(nameUser);

            String message = getString(R.string.keep_plan_for_x_days, activeDaysCount);
            binding.countDayTextLogin.setText(message);

            Glide.with(requireContext())
                    .load(pictureUrl)
                    .into(binding.imageViewAvatar);
        }
    }
}