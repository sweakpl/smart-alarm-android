package com.sweak.smartalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Preferences mPreferences;
    private AlarmSetter mAlarmSetter;
    private TextClock mCurrentTimeText;
    private TextView mAlarmTimeText;
    private Button mStartStopButton;

    private int alarmHour;
    private int alarmMinute;
    private boolean isAlarmPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = new Preferences(getApplication());
        mAlarmSetter = new AlarmSetter();

        restorePreferences();
        findAndAssignViews();
        prepareCurrentTimeText();
        mAlarmSetter.setAlarmTime(alarmHour, alarmMinute);
        setAlarmTimeText(alarmHour, alarmMinute);
        setButtonLabel();
        startStartupAnimation();
        setTimePickerResultListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPreferences.setAlarmPending(AlarmSetter.isAlarmSet(getApplication()));
        if (mPreferences.getAlarmPending()) {
            mPreferences.setAlarmTime(alarmHour, alarmMinute);
        }
    }

    private void restorePreferences() {
        isAlarmPending = mPreferences.getAlarmPending();
        alarmHour = mPreferences.getAlarmHour();
        alarmMinute = mPreferences.getAlarmMinute();
    }

    private void findAndAssignViews() {
        mCurrentTimeText = findViewById(R.id.current_time_text);
        mAlarmTimeText = findViewById(R.id.alarm_time_text);
        mStartStopButton = findViewById(R.id.start_stop_alarm_button);
    }

    private void prepareCurrentTimeText() {
        mCurrentTimeText.setFormat24Hour("HH:mm");
        mCurrentTimeText.setFormat12Hour("HH:mm");
    }

    private void setButtonLabel() {
        if (!isAlarmPending) {
            mStartStopButton.setText(R.string.start_alarm);
        }
        else {
            mStartStopButton.setText(R.string.stop_alarm);
        }
    }

    private void startStartupAnimation() {
        AlphaAnimation animationIn = new AlphaAnimation(0.0f, 1.0f);
        animationIn.setDuration(1000);

        mCurrentTimeText.startAnimation(animationIn);
        mAlarmTimeText.startAnimation(animationIn);
        mStartStopButton.startAnimation(animationIn);
    }

    private void setTimePickerResultListener() {
        getSupportFragmentManager().setFragmentResultListener(TimePickerFragment.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    int alarmHour = bundle.getInt(TimePickerFragment.ALARM_HOUR_KEY);
                    int alarmMinute = bundle.getInt(TimePickerFragment.ALARM_MINUTE_KEY);
                    mAlarmSetter.setAlarmTime(alarmHour, alarmMinute);
                    setAlarmTimeText(alarmHour, alarmMinute);
                }
        );
    }

    private void setAlarmTimeText(int alarmHour, int alarmMinute) {
        this.alarmHour = alarmHour;
        this.alarmMinute = alarmMinute;

        mAlarmTimeText.setText(String.format("Alarm at: %02d:%02d", this.alarmHour, this.alarmMinute));
    }

    public void startOrStopAlarm(View view) {
        if (!isAlarmPending) {
            mAlarmSetter.schedule(getApplicationContext());
            isAlarmPending = AlarmSetter.isAlarmSet(getApplication());

            setAlarmTimeText(alarmHour, alarmMinute);
            setButtonLabel();
        } else {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        }
    }

    public void showTimePickerDialog(View view) {
        if (!isAlarmPending) {
            DialogFragment setAlarmTimeDialog = TimePickerFragment.newInstance(alarmHour, alarmMinute);
            setAlarmTimeDialog.show(getSupportFragmentManager(), "TIME_PICKER_DIALOG");
        }
    }
}