package com.sweak.smartalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

public class App extends Application {
    public static final String PREFERENCE_FILE_KEY = "com.sweak.smartalarm.SHARED_PREFERENCES_KEY";
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "SMART_ALARM_SERVICE_CHANNEL";
    public static final String ACTION_SNOOZE = "com.sweak.smartalarm.ACTION_SNOOZE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "SmartAlarm notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
