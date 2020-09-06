package com.example.firebasedemo1setup;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.sql.Time;
import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activity =this;
        permissions();
    }

    private void permissions()
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET
                + Manifest.permission.READ_EXTERNAL_STORAGE
                + Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        else
            initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED)
            initialize();
        else if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET + Manifest.permission.READ_EXTERNAL_STORAGE + Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissions();
        else
            OpenSetting();
    }

    private void OpenSetting()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission");
        builder.setMessage("Permissions are required");
        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getParent(), "Go to Settings to Grant the Permissions and restart application", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getParent().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }
        })
                .create()
                .show();
    }

    private void initialize() {
        sharedPreferences = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getBoolean("LoginStatus",false))
                {
                    Intent intent = new Intent(SplashActivity.this, ShowData.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}
