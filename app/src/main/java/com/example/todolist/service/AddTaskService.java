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

    @Override
    public void onCreate() {
        super.onCreate();

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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "ACTION_KILL_SERVICE".equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private PendingIntent getAddTaskPendingIntent() {
        Intent addTaskIntent = new Intent(this, MainActivity.class);
        addTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, addTaskIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getDismissPendingIntent() {
        Intent killServiceIntent = new Intent(this, AddTaskService.class);
        killServiceIntent.setAction("ACTION_KILL_SERVICE");
        return PendingIntent.getService(this, 0, killServiceIntent, PendingIntent.FLAG_IMMUTABLE);
    }

}
