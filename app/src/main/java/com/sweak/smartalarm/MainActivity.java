package com.sweak.smartalarm;

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
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import static android.content.Intent.ACTION_SHUTDOWN;
import static com.sweak.smartalarm.App.ACTION_SNOOZE;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preferences mPreferences;
    private AlarmSetter mAlarmSetter;
    private SnoozeReceiver mSnoozeReceiver;
    private ShutdownReceiver mShutdownReceiver;
    private Button mMenuButton;
    private TextClock mCurrentTimeText;
    private TextView mAlarmTimeText;
    private Button mStartStopButton;
    private Button mSnoozeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = new Preferences(getApplication());
        mAlarmSetter = new AlarmSetter();

        registerReceivers();
        restoreSnoozeNumberLeft();
        findAndAssignViews();
        prepareCurrentTimeTextFormat();
        setAlarmTimeText();
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
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mShutdownReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSnoozeReceiver);
        Preferences.unregisterPreferences(this, this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Preferences.PREFERENCES_ALARM_RINGING_KEY:
                setButtonsAppearance();
                if (mPreferences.getAlarmPending() && mPreferences.getAlarmRinging()) {
                    AlphaAnimation animationIn = new AlphaAnimation(0.0f, 1.0f);
                    animationIn.setDuration(1000);
                    if (mPreferences.getSnoozeNumberLeft() >= 1)
                        mSnoozeButton.startAnimation(animationIn);
                }
                break;
            case Preferences.PREFERENCES_SNOOZE_ALARM_SET_KEY:
                if (mPreferences.getSnoozeAlarmPending())
                    setAlarmTimeText();
                break;
            case Preferences.PREFERENCES_ALARM_SET_KEY:
                setButtonsAppearance();
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

    private void findAndAssignViews() {
        mMenuButton = findViewById(R.id.menu_button);
        mCurrentTimeText = findViewById(R.id.current_time_text);
        mAlarmTimeText = findViewById(R.id.alarm_time_text);
        mStartStopButton = findViewById(R.id.start_stop_alarm_button);
        mSnoozeButton = findViewById(R.id.snooze_button);
    }

    private void prepareCurrentTimeTextFormat() {
        mCurrentTimeText.setFormat24Hour("HH:mm");
        mCurrentTimeText.setFormat12Hour("HH:mm");
    }

    private void setAlarmTimeText() {
        if (!mPreferences.getSnoozeAlarmPending())
            mAlarmTimeText.setText(
                    String.format("Alarm at: %02d:%02d",
                            mPreferences.getAlarmHour(), mPreferences.getAlarmMinute()));
        else
            mAlarmTimeText.setText(String.format("Alarm at: %02d:%02d",
                    mPreferences.getSnoozeAlarmHour(), mPreferences.getSnoozeAlarmMinute()));
    }

    private void setButtonsAppearance() {
        mSnoozeButton.setVisibility(View.INVISIBLE);
        if (!mPreferences.getAlarmPending()) {
            mStartStopButton.setText(R.string.start_alarm);
            mSnoozeButton.setClickable(false);
        }
        else {
            mStartStopButton.setText(R.string.stop_alarm);
            mMenuButton.setVisibility(View.GONE);
            if (mPreferences.getAlarmRinging() && mPreferences.getSnoozeNumberLeft() != 0) {
                mSnoozeButton.setVisibility(View.VISIBLE);
                mSnoozeButton.setClickable(true);
            }
            else {
                mSnoozeButton.setClickable(false);
                if (!mPreferences.getSnoozeAlarmPending())
                    disableMenuButton();
            }
        }
    }

    private void startStartupAnimation() {
        AlphaAnimation animationIn = new AlphaAnimation(0.0f, 1.0f);
        animationIn.setDuration(1000);

        mCurrentTimeText.startAnimation(animationIn);
        mAlarmTimeText.startAnimation(animationIn);
        mStartStopButton.startAnimation(animationIn);
        if (!mPreferences.getAlarmPending())
            mMenuButton.startAnimation(animationIn);
        if (mPreferences.getAlarmRinging() && mPreferences.getSnoozeNumberLeft() != 0)
            mSnoozeButton.startAnimation(animationIn);
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

    private void disableMenuButton() {
        mMenuButton.setClickable(false);

        AlphaAnimation animationOut = new AlphaAnimation(1.0f, 0.0f);
        animationOut.setDuration(1000);

        mMenuButton.startAnimation(animationOut);
        new Handler().postDelayed(() -> mMenuButton.setVisibility(View.GONE), 1000);
    }

    public void startOrStopAlarm(View view) {
        if (!mPreferences.getAlarmPending()) {
            mAlarmSetter.schedule(getApplicationContext(), AlarmSetter.REGULAR_ALARM);
        } else {
            Intent intent = new Intent(this, ScanActivity.class);
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