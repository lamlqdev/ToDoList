package com.example.todolist.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.todolist.R;
import com.example.todolist.activity.MainActivity;
import com.example.todolist.application.MyApplication;

public class AddTaskService extends Service {

    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("DISMISS_ACTION".equals(action)) {
                stopForeground(true);
                stopSelf();
            } else if ("ADD_TASK_ACTION".equals(action)) {
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivityIntent);
            }
        }

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_add_task);

        PendingIntent addTaskPendingIntent = getAddTaskPendingIntent();
        PendingIntent dismissPendingIntent = getDismissPendingIntent();

        notificationLayout.setOnClickPendingIntent(R.id.buttonAddTask, addTaskPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.buttonKillService, dismissPendingIntent);

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    private PendingIntent getAddTaskPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("ADD_TASK_ACTION");
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(this, 0, intent, flags);
    }

    private PendingIntent getDismissPendingIntent() {
        Intent intent = new Intent(this, AddTaskService.class);
        intent.setAction("DISMISS_ACTION");
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getService(this, 0, intent, flags);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
