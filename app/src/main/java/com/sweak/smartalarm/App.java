package com.sweak.smartalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

public class App extends Application {
    public static final String PREFERENCE_FILE_KEY = BuildConfig.APPLICATION_ID + ".SHARED_PREFERENCES_KEY";
    public static final String ACTION_SNOOZE = BuildConfig.APPLICATION_ID + ".ACTION_SNOOZE";
    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "SMART_ALARM_SERVICE_CHANNEL";

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
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
