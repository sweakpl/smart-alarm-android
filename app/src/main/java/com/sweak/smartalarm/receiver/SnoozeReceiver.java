package com.sweak.smartalarm.receiver;

import static com.sweak.smartalarm.util.AlarmSetter.SNOOZE_ALARM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sweak.smartalarm.util.AlarmSetter;
import com.sweak.smartalarm.util.Preferences;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {

    private Preferences mPreferences;

    public SnoozeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mPreferences = new Preferences(context);
        AlarmSetter alarmSetter = new AlarmSetter();

        setSnoozePreferences();

        AlarmSetter.cancelAlarm(context, SNOOZE_ALARM);

        alarmSetter.schedule(context, SNOOZE_ALARM);

        if (mPreferences.getSnoozeNumberLeft() != 0)
            mPreferences.setSnoozeNumberLeft(mPreferences.getSnoozeNumberLeft() - 1);
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