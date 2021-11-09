package com.sweak.smartalarm.features.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.sweak.smartalarm.SmartAlarmApplication;
import com.sweak.smartalarm.R;
import com.sweak.smartalarm.features.scan.ScanActivity;
import com.sweak.smartalarm.databinding.ActivityMenuBinding;
import com.sweak.smartalarm.util.AlarmPlayer;
import com.sweak.smartalarm.util.Preferences;

public class MenuActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int WRITE_GALLERY_PERMISSION_REQUEST_CODE = 1;

    private Preferences mPreferences;
    private AlarmPlayer mAlarmPlayer;
    private ActivityMenuBinding mBinding;
    ArrayAdapter<CharSequence> mAlarmToneAdapter;
    ArrayAdapter<CharSequence> mSnoozeDurationAdapter;
    ArrayAdapter<CharSequence> mSnoozeNumberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mPreferences = new Preferences(getApplication());
        mAlarmPlayer = new AlarmPlayer(getApplication());
        prepareViews();
        setSpinnerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAlarmPlayer.stop();
    }

    private void prepareViews() {
        mAlarmToneAdapter = ArrayAdapter.createFromResource(this,
                R.array.alarm_tones, android.R.layout.simple_spinner_item);
        mSnoozeDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.snooze_durations, android.R.layout.simple_spinner_item);
        mSnoozeNumberAdapter = ArrayAdapter.createFromResource(this,
                R.array.snooze_numbers, android.R.layout.simple_spinner_item);

        mAlarmToneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSnoozeDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSnoozeNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.alarmToneSpinner.setAdapter(mAlarmToneAdapter);
        mBinding.snoozeDurationSpinner.setAdapter(mSnoozeDurationAdapter);
        mBinding.snoozeNumberSpinner.setAdapter(mSnoozeNumberAdapter);

        mBinding.alarmToneSpinner.setSelection(mPreferences.getAlarmToneId());
        mBinding.snoozeDurationSpinner.setSelection(mSnoozeDurationAdapter.getPosition(
                String.valueOf(mPreferences.getSnoozeDuration())));
        mBinding.snoozeNumberSpinner.setSelection(mSnoozeNumberAdapter.getPosition(
                String.valueOf(mPreferences.getSnoozeNumber())));
    }

    private void setSpinnerListeners() {
        mBinding.alarmToneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.setAlarmToneId(position);
                mAlarmPlayer.stop();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBinding.snoozeDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.setSnoozeDuration(Integer.parseInt(
                        parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mBinding.snoozeNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int snoozeNumber = Integer.parseInt(parent.getItemAtPosition(position).toString());
                mPreferences.setSnoozeNumber(snoozeNumber);
                mPreferences.setSnoozeNumberLeft(snoozeNumber);
                mBinding.snoozeDurationSpinner.setEnabled(snoozeNumber != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void showAboutDialog(View view) {
        DialogFragment aboutDialog = AboutDialogFragment.newInstance();
        aboutDialog.show(getSupportFragmentManager(), "ABOUT_DIALOG");
    }

    public void saveCodeToGallery(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                insertCodeImage();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_GALLERY_PERMISSION_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this,
                    getString(R.string.code_not_added_to_gallery_version_issue),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void addCustomQRDismissCode(View view) {
        Intent addCodeIntent = new Intent(this, ScanActivity.class);
        addCodeIntent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_SET_DISMISS_CODE);
        startActivity(addCodeIntent);
    }

    public void resetQRDismissCode(View view) {
        mPreferences.setDismissAlarmCode(SmartAlarmApplication.DEFAULT_DISMISS_ALARM_CODE);
        Toast.makeText(this,
                getString(R.string.default_code_added) + " \"" + SmartAlarmApplication.DEFAULT_DISMISS_ALARM_CODE + "\"",
                Toast.LENGTH_LONG)
                .show();
    }

    private void insertCodeImage() {
        String toastMessage;

        String savedUri = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                BitmapFactory.decodeResource(getResources(), R.drawable.qr_code),
                "SmartAlarm QR Code",
                "Code used to turn off the SmartAlarm alarm");

        toastMessage = savedUri != null ?
                getString(R.string.code_added_to_gallery)
                : getString(R.string.code_not_added_to_gallery);

        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                insertCodeImage();
            } else {
                Toast.makeText(this,
                        getString(R.string.code_not_added_to_gallery),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startAlarmPreview(View view) {
        mAlarmPlayer.startPreview(mPreferences.getAlarmToneId());
    }
}