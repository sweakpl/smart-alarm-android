package com.sweak.smartalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final int WRITE_GALLERY_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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

    private void insertCodeImage() {
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                BitmapFactory.decodeResource(   getResources(), R.drawable.qr_code),
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