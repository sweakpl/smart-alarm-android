package com.sweak.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            Preferences preferences = new Preferences(context);

            preferences.setAlarmPending(false);
            preferences.setSnoozeAlarmPending(false);
            preferences.setAlarmRinging(false);
        }
    }
}