package com.sweak.smartalarm.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.sweak.smartalarm.R;

public class TimePickerFragment extends DialogFragment {

    private TimePicker mTimePicker;

    public static final String ALARM_HOUR_KEY = "alarmHour";
    public static final String ALARM_MINUTE_KEY = "alarmMinute";
    public static final String REQUEST_KEY = "timePickerData";

    public TimePickerFragment() {}

    public static TimePickerFragment newInstance(int alarmHour, int alarmMinute) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ALARM_HOUR_KEY, alarmHour);
        args.putInt(ALARM_MINUTE_KEY, alarmMinute);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.set_alarm_time)
                .setPositiveButton(R.string.alert_dialog_ok,
                        (dialog, id) -> {
                            Bundle result = new Bundle();
                            result.putInt(ALARM_HOUR_KEY, mTimePicker.getHour());
                            result.putInt(ALARM_MINUTE_KEY, mTimePicker.getMinute());
                            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                        }
                );

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_time_picker, null);

        mTimePicker = view.findViewById(R.id.alarm_time_picker);
        setTimeDisplayFormat();
        mTimePicker.setHour(getArguments().getInt(ALARM_HOUR_KEY));
        mTimePicker.setMinute(getArguments().getInt(ALARM_MINUTE_KEY));

        dialogBuilder.setView(view);
        return dialogBuilder.create();
    }

    private void setTimeDisplayFormat() {
        if (DateFormat.is24HourFormat(getActivity()))
            mTimePicker.setIs24HourView(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}