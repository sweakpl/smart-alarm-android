package com.sweak.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {

    Preferences mPreferences;

    public SnoozeReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        mPreferences = new Preferences(context);
        AlarmSetter alarmSetter = new AlarmSetter();

        AlarmSetter.cancelAlarm(context);

        setSnoozePreferences();

        alarmSetter.schedule(context, AlarmSetter.SNOOZE_ALARM);
        mPreferences.setSnoozeAlarmPending(true);
    }

    private void setSnoozePreferences() {
        Calendar snoozeCalendar = Calendar.getInstance();
        snoozeCalendar.setTimeInMillis(System.currentTimeMillis());
        snoozeCalendar.set(Calendar.MINUTE,
                snoozeCalendar.get(Calendar.MINUTE) + mPreferences.getSnoozeDuration());

        mPreferences.setSnoozeAlarmTime(
                snoozeCalendar.get(Calendar.HOUR_OF_DAY), snoozeCalendar.get(Calendar.MINUTE));
    }
}