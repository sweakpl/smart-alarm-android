package com.sweak.smartalarm.util;

import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmToneManager {
    public static final int DEFAULT_SYSTEM = 0;
    public static final int GENTLE_GUITAR = 1;
    public static final int SUNNY_DAY = 2;
    public static final int ALARM_CLOCK = 3;
    public static final int ANALOG_WATCH = 4;
    public static final int AIR_HORN_SHORT = 5;
    public static final int AIR_HORN_LONG = 6;
    public static final int SIREN_NOISE = 7;

    public static Uri getAlarmToneUri(int alarmType) {
        switch (alarmType) {
            case GENTLE_GUITAR:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/gentle_guitar");
            case SUNNY_DAY:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/sunny_day");
            case ALARM_CLOCK:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/alarm_clock");
            case ANALOG_WATCH:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/analog_watch");
            case AIR_HORN_SHORT:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/air_horn_short");
            case AIR_HORN_LONG:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/air_horn_long");
            case SIREN_NOISE:
                return Uri.parse("android.resource://com.sweak.smartalarm/raw/siren_noise");
            case DEFAULT_SYSTEM:
            default:
                return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
    }
}
