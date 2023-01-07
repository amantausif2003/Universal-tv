package com.remote.control.allsmarttv.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.databinding.ActivitySplashBinding;


public class StartingScreen extends AppCompatActivity {

    ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

        FirebaseApp.initializeApp(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent(StartingScreen.this, PermActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(StartingScreen.this, FirstActivity.class);
                startActivity(intent);
                finish();
            }

        }, 3000);


    }
}