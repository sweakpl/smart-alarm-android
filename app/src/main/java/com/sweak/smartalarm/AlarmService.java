package com.sweak.smartalarm;

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

import java.io.IOException;

import static com.sweak.smartalarm.App.ACTION_SNOOZE;
import static com.sweak.smartalarm.App.CHANNEL_ID;
import static com.sweak.smartalarm.App.NOTIFICATION_ID;

public class AlarmService extends Service {

    private Preferences mPreferences;
    private NotificationManager mNotificationManager;
    private SnoozeReceiver mSnoozeReceiver;
    private MediaPlayer mMediaPlayer;
    private Notification mNotification;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferences = new Preferences(getApplication());
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mMediaPlayer = new MediaPlayer();

        mPreferences.setSnoozeAlarmPending(false);
        registerSnoozeReceiver();
        prepareNotification();
        prepareMediaPlayer();
    }

    private void registerSnoozeReceiver() {
        mSnoozeReceiver = new SnoozeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE));
    }

    private void prepareNotification() {
        Intent scanIntent = new Intent(this, ScanActivity.class);
        PendingIntent scanPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, scanIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibrationPattern = {0, 1000, 2000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(scanPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_alarm_on)
                .addAction(R.drawable.ic_alarm_on, getString(R.string.stop_alarm), scanPendingIntent)
                .setContentTitle("Alarm off!")
                .setContentText("Time to wake up!")
                .setLights(Color.GREEN, 1000, 1000)
                .setVibrate(vibrationPattern);

        if (mPreferences.getSnoozeNumberLeft() != 0) {
            Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
            snoozeIntent.setAction(ACTION_SNOOZE);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                            snoozeIntent, PendingIntent.FLAG_ONE_SHOT);

            builder.addAction(R.drawable.ic_alarm_snooze, getString(R.string.snooze), snoozePendingIntent);
        }

        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_INSISTENT;
    }

    private void prepareMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(this,
                    AlarmToneManager.getAlarmToneUri(mPreferences.getAlarmToneId()));
            mMediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPreferences.setAlarmRinging(true);

        mMediaPlayer.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, mNotification);
        }
        else {
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }

        wakeUpTheScreen(getApplication());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPreferences.setAlarmRinging(false);

        mMediaPlayer.stop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSnoozeReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}