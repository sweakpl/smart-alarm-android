package com.sweak.smartalarm.activity;

import static com.sweak.smartalarm.App.ACTION_SNOOZE;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sweak.smartalarm.databinding.ActivityLockScreenBinding;
import com.sweak.smartalarm.util.Preferences;

public class LockScreenActivity extends AppCompatActivity {

    private ActivityLockScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeActivityShownOnLock();
        mBinding = ActivityLockScreenBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        prepareSnoozeButton();
    }

    private void makeActivityShownOnLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    private void prepareSnoozeButton() {
        Preferences preferences = new Preferences(this);

        if (preferences.getSnoozeNumberLeft() == 0)
            mBinding.lockSnoozeButton.setEnabled(false);
    }

    public void snoozeAlarm(View view) {
        Intent snoozeIntent = new Intent(ACTION_SNOOZE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(snoozeIntent);
        finish();
    }

    public void stopAlarm(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_DISMISS_ALARM);
        startActivity(intent);

        dismissKeyguard();
    }

    private void dismissKeyguard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        }
    }
}