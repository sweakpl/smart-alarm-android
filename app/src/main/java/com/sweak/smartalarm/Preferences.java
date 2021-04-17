package com.sweak.smartalarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.sweak.smartalarm.App.PREFERENCE_FILE_KEY;

public class Preferences {

    private final SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;

    public final String PREFERENCES_ALARM_SET_KEY = "alarmSet";
    public final String PREFERENCES_SNOOZE_ALARM_SET_KEY = "snoozeAlarmSet";
    public final String PREFERENCES_ALARM_RINGING_KEY = "alarmRinging";
    private final String PREFERENCES_ALARM_HOUR_KEY = "alarmHour";
    private final String PREFERENCES_ALARM_MINUTE_KEY = "alarmMinute";
    private final String PREFERENCES_SNOOZE_ALARM_HOUR_KEY = "snoozeAlarmHour";
    private final String PREFERENCES_SNOOZE_ALARM_MINUTE_KEY = "snoozeAlarmMinute";
    private final String PREFERENCES_SNOOZE_DURATION_KEY = "snoozeDuration";

    public Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
    }

    public static void registerPreferences(Context context,
                                   SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences =
                context.getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPreferences(Context context,
                                           SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences =
                context.getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setAlarmPending(boolean value) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putBoolean(PREFERENCES_ALARM_SET_KEY, value);
        mPreferencesEditor.apply();
    }

    public boolean getAlarmPending() {
        return mSharedPreferences.getBoolean(PREFERENCES_ALARM_SET_KEY, false);
    }

    public void setSnoozeAlarmPending(boolean value) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putBoolean(PREFERENCES_SNOOZE_ALARM_SET_KEY, value);
        mPreferencesEditor.apply();
    }

    public boolean getSnoozeAlarmPending() {
        return mSharedPreferences.getBoolean(PREFERENCES_SNOOZE_ALARM_SET_KEY, false);
    }

    public void setAlarmRinging(boolean value) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putBoolean(PREFERENCES_ALARM_RINGING_KEY, value);
        mPreferencesEditor.apply();
    }

    public boolean getAlarmRinging() {
        return mSharedPreferences.getBoolean(PREFERENCES_ALARM_RINGING_KEY, false);
    }

    public void setAlarmTime(int alarmHour, int alarmMinute) {
        mPreferencesEditor = mSharedPreferences.edit();
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

    public void setSnoozeAlarmTime(int alarmHour, int alarmMinute) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_SNOOZE_ALARM_HOUR_KEY, alarmHour);
        mPreferencesEditor.putInt(PREFERENCES_SNOOZE_ALARM_MINUTE_KEY, alarmMinute);
        mPreferencesEditor.apply();
    }

    public int getSnoozeAlarmHour() {
        Calendar currentDate = Calendar.getInstance();
        int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        return mSharedPreferences.getInt(PREFERENCES_SNOOZE_ALARM_HOUR_KEY, currentHour);
    }

    public int getSnoozeAlarmMinute() {
        Calendar currentDate = Calendar.getInstance();
        int currentMinute = currentDate.get(Calendar.MINUTE);
        return mSharedPreferences.getInt(PREFERENCES_SNOOZE_ALARM_MINUTE_KEY, currentMinute);
    }

    public void setSnoozeDuration(int snoozeDurationMinutes) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_SNOOZE_DURATION_KEY, snoozeDurationMinutes);
        mPreferencesEditor.apply();
    }

    public int getSnoozeDuration() {
        return mSharedPreferences.getInt(PREFERENCES_SNOOZE_DURATION_KEY, 5);
    }
}
