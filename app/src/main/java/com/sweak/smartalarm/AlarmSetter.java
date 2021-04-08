package com.sweak.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import static com.sweak.smartalarm.App.NOTIFICATION_ID;

public class AlarmSetter {

    private Calendar calendar;

    private int alarmHour;
    private int alarmMinute;

    public void setAlarmTime(int alarmHour, int alarmMinute) {
        this.alarmHour = alarmHour;
        this.alarmMinute = alarmMinute;

        setCalendarToAlarmTime();
    }

    private void setCalendarToAlarmTime() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context, NOTIFICATION_ID, intent, 0);

        setAlarm(alarmManager, alarmPendingIntent);
        showToastAlarmSet(context);
    }

    private void setAlarm(AlarmManager alarmManager, PendingIntent alarmPendingIntent) {
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
    }

    private void showToastAlarmSet(Context context) {
        String toastText = String.format("Alarm set for %02d:%02d", alarmHour, alarmMinute);
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }

    public static void cancelAlarm(Context context) {
        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context, NOTIFICATION_ID, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        alarmPendingIntent.cancel();
    }

    public static boolean isAlarmSet(Context context) {
        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        return (PendingIntent.getBroadcast(context, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
    }
}
