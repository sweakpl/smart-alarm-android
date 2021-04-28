package com.sweak.smartalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.sweak.smartalarm.App.PREFERENCE_FILE_KEY;

public class Preferences {

    private final SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;
    private Context mContext;

    public static final String PREFERENCES_ALARM_SET_KEY = "alarmSet";
    public static final String PREFERENCES_SNOOZE_ALARM_SET_KEY = "snoozeAlarmSet";
    public static final String PREFERENCES_ALARM_RINGING_KEY = "alarmRinging";
    private static final String PREFERENCES_DISMISS_ALARM_CODE_KEY = "dismissAlarmCode";
    private static final String PREFERENCES_ALARM_HOUR_KEY = "alarmHour";
    private static final String PREFERENCES_ALARM_MINUTE_KEY = "alarmMinute";
    private static final String PREFERENCES_ALARM_TIME_STRING_KEY = "alarmTimeString";
    private static final String PREFERENCES_SNOOZE_ALARM_HOUR_KEY = "snoozeAlarmHour";
    private static final String PREFERENCES_SNOOZE_ALARM_MINUTE_KEY = "snoozeAlarmMinute";
    private static final String PREFERENCES_SNOOZE_ALARM_TIME_STRING_KEY = "snoozeAlarmTimeString";
    private static final String PREFERENCES_SNOOZE_DURATION_KEY = "snoozeDuration";
    private static final String PREFERENCES_SNOOZE_NUMBER_KEY = "snoozeNumber";
    private static final String PREFERENCES_SNOOZE_NUMBER_LEFT_KEY = "snoozeNumberLeft";
    private static final String PREFERENCES_ALARM_TONE_ID_KEY = "alarmTone";

    public Preferences(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
    }

    public static void registerPreferences(@NonNull Context context,
                                           SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences =
                context.getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPreferences(@NonNull Context context,
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

    public void setDismissAlarmCode(String code) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putString(PREFERENCES_DISMISS_ALARM_CODE_KEY, code);
        mPreferencesEditor.apply();
    }

    public String getDismissAlarmCode() {
        return mSharedPreferences.getString(PREFERENCES_DISMISS_ALARM_CODE_KEY,
                App.DEFAULT_DISMISS_ALARM_CODE);
    }

    public void setAlarmTime(int alarmHour, int alarmMinute) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_ALARM_HOUR_KEY, alarmHour);
        mPreferencesEditor.putInt(PREFERENCES_ALARM_MINUTE_KEY, alarmMinute);
        mPreferencesEditor.apply();
    }

    public String getAlarmTime() {
        String time24 = String.format("%02d:%02d", getAlarmHour(), getAlarmMinute());
        return getCurrentLocaleTimeString(time24);
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

    public String getSnoozeAlarmTime() {
        String time24 = String.format("%02d:%02d", getSnoozeAlarmHour(), getSnoozeAlarmMinute());
        return getCurrentLocaleTimeString(time24);
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

    public void setSnoozeNumber(int snoozeNumber) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_SNOOZE_NUMBER_KEY, snoozeNumber);
        mPreferencesEditor.apply();
    }

    public int getSnoozeNumber() {
        return mSharedPreferences.getInt(PREFERENCES_SNOOZE_NUMBER_KEY, 3);
    }

    public void setSnoozeNumberLeft(int snoozeNumberLeft) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_SNOOZE_NUMBER_LEFT_KEY, snoozeNumberLeft);
        mPreferencesEditor.apply();
    }

    public int getSnoozeNumberLeft() {
        return mSharedPreferences.getInt(PREFERENCES_SNOOZE_NUMBER_LEFT_KEY, 0);
    }

    public void setAlarmToneId(int alarmTone) {
        mPreferencesEditor = mSharedPreferences.edit();
        mPreferencesEditor.putInt(PREFERENCES_ALARM_TONE_ID_KEY, alarmTone);
        mPreferencesEditor.apply();
    }

    public int getAlarmToneId() {
        return mSharedPreferences.getInt(PREFERENCES_ALARM_TONE_ID_KEY,
                AlarmToneManager.DEFAULT_SYSTEM);
    }

    // helper methods

    private String getCurrentLocaleTimeString(String time24hourFormat) {
        if (DateFormat.is24HourFormat(mContext))
            return time24hourFormat;
        else {
            SimpleDateFormat time24SDF = new SimpleDateFormat("H:mm");
            SimpleDateFormat time12SDF = new SimpleDateFormat("h:mm a");
            Date time24Date = get24hourFormatDate(time24hourFormat, time24SDF);
            return time12SDF.format(time24Date);
        }
    }

    private Date get24hourFormatDate(String time24, @NonNull SimpleDateFormat time24SDF) {
        Date date = new Date();
        try {
            date = time24SDF.parse(time24);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
