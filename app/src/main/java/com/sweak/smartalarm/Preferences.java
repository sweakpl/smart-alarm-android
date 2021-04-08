package com.sweak.smartalarm;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPreferencesEditor;

    private String PREFERENCES_ALARM_SET_KEY = "alarmSet";
    private String PREFERENCES_ALARM_HOUR_KEY = "alarmHour";
    private String PREFERENCES_ALARM_MINUTE_KEY = "alarmMinute";

    public Preferences(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_key), MODE_PRIVATE);
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
        return mSharedPreferences.getInt(PREFERENCES_ALARM_HOUR_KEY, 0);
    }

    public int getAlarmMinute() {
        return mSharedPreferences.getInt(PREFERENCES_ALARM_MINUTE_KEY, 0);
    }
}
