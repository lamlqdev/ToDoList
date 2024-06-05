package com.example.todolist.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todolist.R;
import com.example.todolist.databinding.ActivityMainBinding;
import com.example.todolist.fragment.CalendarFragment;
import com.example.todolist.fragment.MineFragment;
import com.example.todolist.fragment.TasksFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        switchFragment(new TasksFragment());
        setupBottomNavigationViewMenu();
    }

    private void setupBottomNavigationViewMenu() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.tasks) {
                switchFragment(new TasksFragment());
                return true;
            }
            if (item.getItemId() == R.id.calendar) {
                switchFragment(new CalendarFragment());
                return true;
            }
            if (item.getItemId() == R.id.mine) {
                switchFragment(new MineFragment());
                return true;
            }
            return true;
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}