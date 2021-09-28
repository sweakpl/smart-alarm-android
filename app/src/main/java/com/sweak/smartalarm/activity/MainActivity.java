package com.sweak.smartalarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.sweak.smartalarm.databinding.ActivityMainBinding;
import com.sweak.smartalarm.util.AlarmSetter;
import com.sweak.smartalarm.util.Preferences;
import com.sweak.smartalarm.R;
import com.sweak.smartalarm.receiver.ShutdownReceiver;
import com.sweak.smartalarm.receiver.SnoozeReceiver;
import com.sweak.smartalarm.fragment.TimePickerFragment;

import static android.content.Intent.ACTION_SHUTDOWN;
import static com.sweak.smartalarm.App.ACTION_SNOOZE;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preferences mPreferences;
    private AlarmSetter mAlarmSetter;
    private SnoozeReceiver mSnoozeReceiver;
    private ShutdownReceiver mShutdownReceiver;
    private ActivityMainBinding mBinding;
    private AlphaAnimation mAnimationIn;
    private AlphaAnimation mAnimationOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mPreferences = new Preferences(getApplication());
        mAlarmSetter = new AlarmSetter();

        registerReceivers();
        restoreSnoozeNumberLeft();
        setAlarmTimeText();
        prepareAnimations();
        setButtonsAppearance();
        startStartupAnimation();
        setTimePickerResultListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Preferences.registerPreferences(this, this);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mSnoozeReceiver);
            Preferences.unregisterPreferences(this, this);
        }
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Preferences.PREFERENCES_ALARM_RINGING_KEY:
                setButtonsAppearance();
                if (mPreferences.getAlarmPending() && mPreferences.getAlarmRinging())
                    showSnoozeButtonAnimation();
                break;
            case Preferences.PREFERENCES_ALARM_SET_KEY:
                setButtonsAppearance();
                if (mPreferences.getAlarmPending())
                    disableMenuButton();
                else
                    enableMenuButton();
                break;
            case Preferences.PREFERENCES_SNOOZE_ALARM_SET_KEY:
                if (mPreferences.getSnoozeAlarmPending())
                    setAlarmTimeText();
                break;
        }
    }

    private void registerReceivers() {
        mSnoozeReceiver = new SnoozeReceiver();
        mShutdownReceiver = new ShutdownReceiver();

        this.registerReceiver(mShutdownReceiver, new IntentFilter(ACTION_SHUTDOWN));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE));
    }

    private void restoreSnoozeNumberLeft() {
        if (!mPreferences.getAlarmPending())
            mPreferences.setSnoozeNumberLeft(mPreferences.getSnoozeNumber());
    }

    private void setAlarmTimeText() {
        if (!mPreferences.getSnoozeAlarmPending())
            mBinding.alarmTimeText.setText(getString(R.string.alarm_at, mPreferences.getAlarmTimeString()));
        else
            mBinding.alarmTimeText.setText(getString(R.string.alarm_at, mPreferences.getSnoozeAlarmTimeString()));
    }

    private void setButtonsAppearance() {
        mBinding.snoozeButton.setVisibility(View.INVISIBLE);
        if (!mPreferences.getAlarmPending()) {
            mBinding.startStopAlarmButton.setText(R.string.start_alarm);
            mBinding.snoozeButton.setClickable(false);
            mBinding.menuButton.setVisibility(View.VISIBLE);
        } else {
            mBinding.startStopAlarmButton.setText(R.string.stop_alarm);
            mBinding.menuButton.setVisibility(View.INVISIBLE);
            if (mPreferences.getAlarmRinging() && mPreferences.getSnoozeNumberLeft() != 0) {
                mBinding.snoozeButton.setVisibility(View.VISIBLE);
                mBinding.snoozeButton.setClickable(true);
            } else {
                mBinding.snoozeButton.setClickable(false);
                if (!mPreferences.getSnoozeAlarmPending())
                    mBinding.menuButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void prepareAnimations() {
        mAnimationIn = new AlphaAnimation(0.0f, 1.0f);
        mAnimationIn.setDuration(1000);

        mAnimationOut = new AlphaAnimation(1.0f, 0.0f);
        mAnimationOut.setDuration(1000);
    }

    private void startStartupAnimation() {
        mBinding.currentTimeText.startAnimation(mAnimationIn);
        mBinding.alarmTimeText.startAnimation(mAnimationIn);
        mBinding.startStopAlarmButton.startAnimation(mAnimationIn);
        if (!mPreferences.getAlarmPending())
            mBinding.menuButton.startAnimation(mAnimationIn);
        if (mPreferences.getAlarmRinging() && mPreferences.getSnoozeNumberLeft() != 0)
            mBinding.snoozeButton.startAnimation(mAnimationIn);
    }

    private void setTimePickerResultListener() {
        getSupportFragmentManager().setFragmentResultListener(TimePickerFragment.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    int alarmHour = bundle.getInt(TimePickerFragment.ALARM_HOUR_KEY);
                    int alarmMinute = bundle.getInt(TimePickerFragment.ALARM_MINUTE_KEY);
                    mPreferences.setAlarmTime(alarmHour, alarmMinute);
                    setAlarmTimeText();
                }
        );
    }

    private void showSnoozeButtonAnimation() {
        if (mPreferences.getSnoozeNumberLeft() >= 1)
            mBinding.snoozeButton.startAnimation(mAnimationIn);
    }

    private void enableMenuButton() {
        mBinding.menuButton.setClickable(true);

        mBinding.menuButton.startAnimation(mAnimationIn);
        new Handler().postDelayed(() -> mBinding.menuButton.setVisibility(View.VISIBLE), 1000);
    }

    private void disableMenuButton() {
        mBinding.menuButton.setClickable(false);

        mBinding.menuButton.startAnimation(mAnimationOut);
        new Handler().postDelayed(() -> mBinding.menuButton.setVisibility(View.INVISIBLE), 1000);
    }

    public void startOrStopAlarm(View view) {
        if (!mPreferences.getAlarmPending()) {
            mAlarmSetter.schedule(getApplicationContext(), AlarmSetter.REGULAR_ALARM,
                    findViewById(R.id.main_layout));
        } else {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_DISMISS_ALARM);
            startActivity(intent);
        }
    }

    public void snoozeAlarm(View view) {
        Intent snoozeIntent = new Intent(ACTION_SNOOZE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(snoozeIntent);
    }

    public void showTimePickerDialog(View view) {
        if (!mPreferences.getAlarmPending()) {
            DialogFragment setAlarmTimeDialog = TimePickerFragment.newInstance(
                    mPreferences.getAlarmHour(), mPreferences.getAlarmMinute());
            setAlarmTimeDialog.show(getSupportFragmentManager(), "TIME_PICKER_DIALOG");
        }
    }

    public void showMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}