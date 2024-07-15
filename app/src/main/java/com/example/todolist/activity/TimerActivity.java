package com.example.todolist.activity;

import android.os.Bundle;
import android.os.CountDownTimer;

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
    private int totalTimeInMillis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MinutePickerDialogFragment minutePickerDialogFragment = MinutePickerDialogFragment.newInstance();
        minutePickerDialogFragment.setOnMinuteSelectedListener(this);
        minutePickerDialogFragment.show(getSupportFragmentManager(), "MinutePickerDialogFragment");
    }

    @Override
    public void onMinuteSelected(int selectedMinutes) {
        if (selectedMinutes > 0) {
            totalTimeInMillis = selectedMinutes * 60 * 1000;
            startCountdown(totalTimeInMillis);
        } else {
            finish();
        }
    }

    private void startCountdown(int totalTimeInMillis) {
        binding.progressCircular.setMax(totalTimeInMillis);
        countDownTimer = new CountDownTimer(totalTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeLeftFormatted = String.format("%02d : %02d", minutes, seconds);
                binding.textViewTimeLeft.setText(timeLeftFormatted);
                binding.progressCircular.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                binding.textViewTimeLeft.setText("00 : 00");
                binding.progressCircular.setProgress(0);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}