package com.sweak.smartalarm.activity;

import static com.sweak.smartalarm.util.AlarmSetter.REGULAR_ALARM;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.zxing.BarcodeFormat;
import com.sweak.smartalarm.R;
import com.sweak.smartalarm.databinding.ActivityScanBinding;
import com.sweak.smartalarm.util.AlarmSetter;
import com.sweak.smartalarm.util.Preferences;

import java.util.Collections;

public class ScanActivity extends AppCompatActivity {

    public static final String SCAN_MODE_KEY = "ScanMode";
    public static final int MODE_DISMISS_ALARM = 0;
    public static final int MODE_SET_DISMISS_CODE = 1;

    private ActivityScanBinding mBinding;
    private CodeScanner mCodeScanner;
    private int mScanMode;
    private Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDismissKeyguard();

        mBinding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mPreferences = new Preferences(getApplication());

        retrieveScanMode();
        checkForPermissionsAndStartScanning();
    }

    private void setDismissKeyguard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void checkForPermissionsAndStartScanning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            startScanning();
        }
    }

    private void retrieveScanMode() {
        Intent intent = getIntent();
        mScanMode = intent.getIntExtra(SCAN_MODE_KEY, MODE_DISMISS_ALARM);
    }

    private void startScanning() {
        mCodeScanner = new CodeScanner(this, mBinding.scannerView);
        setScanFormatToQR();

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            if (mScanMode == MODE_DISMISS_ALARM) {
                if (result.getText().equals(mPreferences.getDismissAlarmCode())) {
                    AlarmSetter.cancelAlarm(getApplication(), REGULAR_ALARM);
                    finishAffinity();
                } else {
                    showWrongCodeToast(result.getText());
                }
            } else if (mScanMode == MODE_SET_DISMISS_CODE) {
                mPreferences.setDismissAlarmCode(result.getText());
                showCodeAdditionToast();
                finish();
            }
        }));

        mBinding.scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void setScanFormatToQR() {
        mCodeScanner.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
    }

    private void showWrongCodeToast(String code) {
        Toast.makeText(this,
                getString(R.string.wrong_code) + " \"" + code + "\" " +
                        getString(R.string.tap_to_try_again),
                Toast.LENGTH_LONG)
                .show();
    }

    private void showCodeAdditionToast() {
        Toast.makeText(this,
                getString(R.string.new_code_added) + " \"" + mPreferences.getDismissAlarmCode() + "\"",
                Toast.LENGTH_LONG)
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.camera_granted), Toast.LENGTH_LONG).show();
                startScanning();
            } else {
                Toast.makeText(this, getString(R.string.camera_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }
}