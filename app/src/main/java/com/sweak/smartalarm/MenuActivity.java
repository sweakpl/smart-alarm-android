package com.sweak.smartalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int WRITE_GALLERY_PERMISSION_REQUEST_CODE = 1;

    private Preferences mPreferences;
    Spinner mAlarmToneSpinner;
    Spinner mSnoozeDurationSpinner;
    Spinner mSnoozeNumberSpinner;
    ArrayAdapter<CharSequence> mAlarmToneAdapter;
    ArrayAdapter<CharSequence> mSnoozeDurationAdapter;
    ArrayAdapter<CharSequence> mSnoozeNumberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mPreferences = new Preferences(getApplication());
        findAndAssignViews();
        prepareViews();
        setSpinnerListeners();
    }

    private void findAndAssignViews() {
        mAlarmToneSpinner = findViewById(R.id.alarm_tone_spinner);
        mSnoozeDurationSpinner = findViewById(R.id.snooze_duration_spinner);
        mSnoozeNumberSpinner = findViewById(R.id.snooze_number_spinner);
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

        mAlarmToneSpinner.setAdapter(mAlarmToneAdapter);
        mSnoozeDurationSpinner.setAdapter(mSnoozeDurationAdapter);
        mSnoozeNumberSpinner.setAdapter(mSnoozeNumberAdapter);

        mAlarmToneSpinner.setSelection(mPreferences.getAlarmToneId());
        mSnoozeDurationSpinner.setSelection(mSnoozeDurationAdapter.getPosition(
                String.valueOf(mPreferences.getSnoozeDuration())));
        mSnoozeNumberSpinner.setSelection(mSnoozeNumberAdapter.getPosition(
                String.valueOf(mPreferences.getSnoozeNumber())));
    }

    private void setSpinnerListeners() {
        mAlarmToneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.setAlarmToneId(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mSnoozeDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.setSnoozeDuration(Integer.parseInt(
                        parent.getItemAtPosition(position).toString()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mSnoozeNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int snoozeNumber = Integer.parseInt(parent.getItemAtPosition(position).toString());
                mPreferences.setSnoozeNumber(snoozeNumber);
                mPreferences.setSnoozeNumberLeft(snoozeNumber);
                mSnoozeDurationSpinner.setEnabled(snoozeNumber != 0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void showAboutDialog(View view) {
        DialogFragment aboutDialog = AboutDialogFragment.newInstance();
        aboutDialog.show(getSupportFragmentManager(), "ABOUT_DIALOG");
    }

    public void saveCodeToGallery(View view) {
        if (hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            insertCodeImage();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void addCustomQRDismissCode(View view) {
        Intent addCodeIntent = new Intent(this, ScanActivity.class);
        addCodeIntent.putExtra(this.getPackageName() + ScanActivity.SCAN_MODE_KEY,
                ScanActivity.SET_DISMISS_CODE_MODE);
        startActivity(addCodeIntent);
    }

    public void resetQRDismissCode(View view) {
        mPreferences.setDismissAlarmCode(App.DEFAULT_DISMISS_ALARM_CODE);
        Toast.makeText(this,
                "Default code to dismiss alarm added: \"" + App.DEFAULT_DISMISS_ALARM_CODE + "\"",
                Toast.LENGTH_LONG)
                .show();
    }

    private void insertCodeImage() {
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                BitmapFactory.decodeResource(getResources(), R.drawable.qr_code),
                "SmartAlarm QR Code",
                "Code used to turn off the SmartAlarm alarm");

        Toast.makeText(this,
                "QR code successfully added to Your gallery, now go and put it somewhere!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                insertCodeImage();
            }
        }
        else {
            Toast.makeText(this,
                    "Can't write to gallery - QR code not saved!",
                    Toast.LENGTH_LONG).show();
        }
    }
}