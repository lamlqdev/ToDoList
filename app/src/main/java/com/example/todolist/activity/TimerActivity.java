package com.example.todolist.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.example.todolist.databinding.ActivityTimerBinding;
import com.example.todolist.fragment.MinutePickerDialogFragment;

public class TimerActivity extends AppCompatActivity implements MinutePickerDialogFragment.OnMinuteSelectedListener {
    private ActivityTimerBinding binding;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean isTimerRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MinutePickerDialogFragment minutePickerDialogFragment = MinutePickerDialogFragment.newInstance();
        minutePickerDialogFragment.setOnMinuteSelectedListener(this);
        minutePickerDialogFragment.show(getSupportFragmentManager(), "MinutePickerDialogFragment");

        setEvents();
    }

    private void setEvents() {
        binding.buttonPauseOrContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseCountdown();
                } else {
                    continueCountdown();
                }
            }
        });

        binding.buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void continueCountdown() {
        startCountdown(timeLeftInMillis);
        isTimerRunning = true;
        binding.buttonPauseOrContinue.setText(R.string.pause);
        binding.textViewPaused.setVisibility(View.GONE);
        binding.textViewTimeLeft.setTextSize(36);
        binding.buttonEnd.setVisibility(View.GONE);
    }

    private void pauseCountdown() {
        countDownTimer.cancel();
        isTimerRunning = false;
        binding.buttonPauseOrContinue.setText(R.string.continue_count_down);
        binding.textViewPaused.setVisibility(View.VISIBLE);
        binding.textViewTimeLeft.setTextSize(22);
        binding.buttonEnd.setVisibility(View.VISIBLE);
    }

    private void startCountdown(long totalTimeInMillis) {
        binding.progressCircular.setMax((int) totalTimeInMillis);
        countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownUI();
            }

            @Override
            public void onFinish() {
                binding.textViewTimeLeft.setText("00 : 00");
                binding.progressCircular.setProgress(0);
                Toast.makeText(TimerActivity.this, "Time focus finished!", Toast.LENGTH_SHORT).show();
                isTimerRunning = false;
                binding.buttonPauseOrContinue.setVisibility(View.GONE);
                binding.buttonEnd.setVisibility(View.GONE);
                binding.textViewPaused.setVisibility(View.GONE);
            }
        }.start();
        isTimerRunning = true;
    }

    private void updateCountdownUI() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d : %02d", minutes, seconds);
        binding.textViewTimeLeft.setText(timeLeftFormatted);
        binding.progressCircular.setProgress((int) timeLeftInMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }

    @Override
    public void onMinuteSelected(int selectedMinutes) {
        if (selectedMinutes > 0) {
            timeLeftInMillis = (long) selectedMinutes * 60 * 1000;
            startCountdown(timeLeftInMillis);
        } else {
            finish();
        }
    }
}