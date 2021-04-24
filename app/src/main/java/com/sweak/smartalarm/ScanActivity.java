package com.sweak.smartalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

public class ScanActivity extends AppCompatActivity {

    public static final String SCAN_MODE_KEY = "ScanMode";
    public static final int DISMISS_ALARM_MODE = 0;
    public static final int SET_DISMISS_CODE_MODE = 1;

    private int mScanMode;
    private CodeScanner mCodeScanner;
    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mPreferences = new Preferences(getApplication());

        retrieveScanMode();
        checkForPermissionsAndStartScanning();
    }

    private void checkForPermissionsAndStartScanning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, 0);
        } else {
            startScanning();
        }
    }

    private void retrieveScanMode() {
        Intent intent = getIntent();
        mScanMode = intent.getIntExtra(getApplication().getPackageName() + SCAN_MODE_KEY,
                DISMISS_ALARM_MODE);
    }

    private void startScanning() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            if (mScanMode == DISMISS_ALARM_MODE) {
                if (result.getText().equals(mPreferences.getDismissAlarmCode())) {
                    AlarmSetter.cancelAlarm(getApplication());
                    mPreferences.setAlarmPending(false);
                    mPreferences.setSnoozeAlarmPending(false);
                    finishAffinity();
                }
                else {
                    showWrongCodeToast(result.getText());
                }
            }
            else if (mScanMode == SET_DISMISS_CODE_MODE) {
                mPreferences.setDismissAlarmCode(result.getText());
                showCodeAdditionToast();
                finish();
            }
        }));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void showWrongCodeToast(String code) {
        Toast.makeText(this,
                "Wrong code! \"" + code + "\" Tap to try again!",
                Toast.LENGTH_LONG)
                .show();
    }

    private void showCodeAdditionToast() {
        Toast.makeText(this,
                "New code to dismiss alarm added: \"" + mPreferences.getDismissAlarmCode() + "\"",
                Toast.LENGTH_LONG)
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if(mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }
}