package com.sweak.smartalarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mPreferencesEditor;

    private final String PREFERENCES_ALARM_SET_KEY = "alarmSet";
    private final String PREFERENCES_ALARM_HOUR_KEY = "alarmHour";
    private final String PREFERENCES_ALARM_MINUTE_KEY = "alarmMinute";

    public Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), MODE_PRIVATE);
        mPreferencesEditor = mSharedPreferences.edit();
    }

    public void setAlarmPending(boolean value) {
        mPreferencesEditor.putBoolean(PREFERENCES_ALARM_SET_KEY, value);
        mPreferencesEditor.apply();
    }

    public boolean getAlarmPending() {
        return mSharedPreferences.getBoolean(PREFERENCES_ALARM_SET_KEY, false);
    }

    public void setAlarmTime(int alarmHour, int alarmMinute) {
        mPreferencesEditor.putInt(PREFERENCES_ALARM_HOUR_KEY, alarmHour);
        mPreferencesEditor.putInt(PREFERENCES_ALARM_MINUTE_KEY, alarmMinute);
        mPreferencesEditor.apply();
    }

    public int getAlarmHour() {
        Calendar currentDate = Calendar.getInstance();
        int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        return mSharedPreferences.getInt(PREFERENCES_ALARM_HOUR_KEY, currentHour);
    }

    public int getAlarmMinute() {
        Calendar currentDate = Calendar.getInstance();
        int currentMinute = currentDate.get(Calendar.MINUTE);
        return mSharedPreferences.getInt(PREFERENCES_ALARM_MINUTE_KEY, currentMinute);
    }
}
