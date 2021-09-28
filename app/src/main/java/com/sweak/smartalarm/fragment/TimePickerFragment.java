package com.sweak.smartalarm.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.sweak.smartalarm.R;
import com.sweak.smartalarm.databinding.FragmentTimePickerBinding;

public class TimePickerFragment extends DialogFragment {

    private FragmentTimePickerBinding mBinding;

    public static final String ALARM_HOUR_KEY = "alarmHour";
    public static final String ALARM_MINUTE_KEY = "alarmMinute";
    public static final String REQUEST_KEY = "timePickerData";

    public TimePickerFragment() {
    }

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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.set_alarm_time)
                .setPositiveButton(R.string.alert_dialog_ok,
                        (dialog, id) -> {
                            Bundle result = new Bundle();
                            result.putInt(ALARM_HOUR_KEY, mBinding.alarmTimePicker.getHour());
                            result.putInt(ALARM_MINUTE_KEY, mBinding.alarmTimePicker.getMinute());
                            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                        }
                );

        mBinding = FragmentTimePickerBinding.inflate(requireActivity().getLayoutInflater());

        setTimeDisplayFormat();
        mBinding.alarmTimePicker.setHour(getArguments().getInt(ALARM_HOUR_KEY));
        mBinding.alarmTimePicker.setMinute(getArguments().getInt(ALARM_MINUTE_KEY));

        dialogBuilder.setView(mBinding.getRoot());
        return dialogBuilder.create();
    }

    private void setTimeDisplayFormat() {
        if (DateFormat.is24HourFormat(getActivity()))
            mBinding.alarmTimePicker.setIs24HourView(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}