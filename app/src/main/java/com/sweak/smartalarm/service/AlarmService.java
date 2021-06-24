package com.sweak.smartalarm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sweak.smartalarm.activity.LockScreenActivity;
import com.sweak.smartalarm.util.AlarmPlayer;
import com.sweak.smartalarm.util.AlarmToneManager;
import com.sweak.smartalarm.util.Preferences;
import com.sweak.smartalarm.R;
import com.sweak.smartalarm.receiver.SnoozeReceiver;
import com.sweak.smartalarm.activity.ScanActivity;

import java.io.IOException;

import static com.sweak.smartalarm.App.ACTION_SNOOZE;
import static com.sweak.smartalarm.App.CHANNEL_ID;
import static com.sweak.smartalarm.App.NOTIFICATION_ID;

public class AlarmService extends Service {

    private Preferences mPreferences;
    private NotificationManager mNotificationManager;
    private AlarmPlayer mAlarmPlayer;
    private SnoozeReceiver mSnoozeReceiver;
    private Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferences = new Preferences(getApplication());
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mAlarmPlayer = new AlarmPlayer(this);

        mPreferences.setSnoozeAlarmPending(false);
        registerSnoozeReceiver();
        prepareNotification();
    }

    private void registerSnoozeReceiver() {
        mSnoozeReceiver = new SnoozeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE));
    }

    private void prepareNotification() {
        long[] vibrationPattern = {0, 1000, 2000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_alarm_on)
                .setContentTitle(getString(R.string.alarm_title))
                .setContentText(getString(R.string.alarm_description))
                .setLights(Color.GREEN, 1000, 1000)
                .setVibrate(vibrationPattern);

        setNotificationIntents(builder);

        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_INSISTENT;
    }

    private void setNotificationIntents(NotificationCompat.Builder builder) {
        Intent scanIntent = new Intent(this, ScanActivity.class);
        scanIntent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_DISMISS_ALARM);
        PendingIntent scanPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, scanIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                        snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(scanPendingIntent);
        builder.addAction(R.drawable.ic_alarm_on, getString(R.string.stop_alarm), scanPendingIntent);
        if (mPreferences.getSnoozeNumberLeft() != 0)
            builder.addAction(R.drawable.ic_alarm_snooze, getString(R.string.snooze), snoozePendingIntent);

        // full screen intent
        Intent lockScreenIntent = new Intent(getApplication(), LockScreenActivity.class);
        PendingIntent lockScreenPendingIntent =
                PendingIntent.getActivity(this, NOTIFICATION_ID, lockScreenIntent, 0);
        builder.setFullScreenIntent(lockScreenPendingIntent, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPreferences.setAlarmRinging(true);

        mAlarmPlayer.startAlarm(mPreferences.getAlarmToneId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, mNotification);
        }
        else {
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }

        wakeUpTheScreen(getApplication());

        return START_STICKY;
    }

    private void wakeUpTheScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isInteractive();
        if (!isScreenOn) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "App:AlarmReceiver");
            wakeLock.acquire(10000);
        }
    }

    @Override
    public void onDestroy() {
        mPreferences.setAlarmRinging(false);
        mAlarmPlayer.stop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSnoozeReceiver);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}