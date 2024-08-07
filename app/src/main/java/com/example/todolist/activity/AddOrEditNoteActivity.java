package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.DAO.NoteDAOImpl;
import com.example.todolist.databinding.ActivityAddOrEditNoteBinding;
import com.example.todolist.model.Note;
import com.example.todolist.model.Task;

public class AddOrEditNoteActivity extends AppCompatActivity {
    public static final int RESULT_OK = 1;
    private ActivityAddOrEditNoteBinding binding;
    private NoteDAOImpl noteDAOImpl;
    private Task selectedTask;
    private Note note;
    private boolean isUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditNoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            selectedTask = (Task) intent.getSerializableExtra("task");
        }

        initializeData();
        setWidgets();
        setEvents();
        setupBackPressedCallback();
    }

    private void initializeData() {
        noteDAOImpl = new NoteDAOImpl(this);
        note = noteDAOImpl.getNoteByTaskID(selectedTask.getTaskID());
    }

    private void setWidgets() {
        binding.textTitle.setText(selectedTask.getTitle());

        if (note != null) {
            isUpdate = true;
            binding.editTextNotes.setText(note.getContent());
        }

        binding.editTextNotes.requestFocus();
    }

    private void setEvents() {
        binding.editTextNotes.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isUpdate) {
                    note.setContent(binding.editTextNotes.getText().toString());
                    noteDAOImpl.updateNote(note);
                } else {
                    note = new Note();
                    note.setContent(binding.editTextNotes.getText().toString());
                    note.setTaskID(selectedTask.getTaskID());
                    noteDAOImpl.addNote(note);
                }
            }
        });

        binding.buttonBack.setOnClickListener(v -> {
            binding.editTextNotes.clearFocus();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                binding.editTextNotes.clearFocus();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}