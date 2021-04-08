package com.sweak.smartalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Preferences mPreferences;
    private TextView mAlarmTimeText;
    private Button mStartStopButton;
    private AlarmSetter mAlarmSetter;

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
        setAppropriateAlarmText();
        setButtonLabel();
        setTimePickerResultListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPreferences.setAlarmPending(mAlarmSetter.isAlarmSet(getApplication()));
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
        mAlarmTimeText = findViewById(R.id.alarm_time_text);
        mStartStopButton = findViewById(R.id.start_stop_alarm_button);
    }

    private void setAppropriateAlarmText() {
        if (!isAlarmPending) {
            Calendar currentDate = Calendar.getInstance();
            alarmHour = currentDate.get(Calendar.HOUR_OF_DAY);
            alarmMinute = currentDate.get(Calendar.MINUTE);

            mAlarmTimeText.setClickable(true);
        }
        else
            mAlarmTimeText.setClickable(false);

        mAlarmSetter.setAlarmTime(alarmHour, alarmMinute);
        setNewAlarmTime(alarmHour, alarmMinute);
    }

    private void setButtonLabel() {
        if (!isAlarmPending) {
            mStartStopButton.setText(R.string.start_alarm);
        }
        else {
            mStartStopButton.setText(R.string.stop_alarm);
        }
    }

    private void setTimePickerResultListener() {
        getSupportFragmentManager().setFragmentResultListener(TimePickerFragment.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    int alarmHour = bundle.getInt(TimePickerFragment.ALARM_HOUR_KEY);
                    int alarmMinute = bundle.getInt(TimePickerFragment.ALARM_MINUTE_KEY);
                    setNewAlarmTime(alarmHour, alarmMinute);
                }
        );
    }

    private void setNewAlarmTime(int alarmHour, int alarmMinute) {
        this.alarmHour = alarmHour;
        this.alarmMinute = alarmMinute;

        mAlarmSetter.setAlarmTime(this.alarmHour, this.alarmMinute);

        mAlarmTimeText.setText(String.format("%02d:%02d", this.alarmHour, this.alarmMinute));
    }

    public void startOrStopAlarm(View view) {
        if (!isAlarmPending) {
            mAlarmSetter.schedule(getApplicationContext());
            mAlarmTimeText.setClickable(false);
        }
        else {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        }

        isAlarmPending = mAlarmSetter.isAlarmSet(getApplication());
        setButtonLabel();
    }

    public void showTimePickerDialog(View view) {
        DialogFragment setAlarmTimeDialog = TimePickerFragment.newInstance(alarmHour, alarmMinute);
        setAlarmTimeDialog.show(getSupportFragmentManager(), "TIME_PICKER_DIALOG");
    }
}