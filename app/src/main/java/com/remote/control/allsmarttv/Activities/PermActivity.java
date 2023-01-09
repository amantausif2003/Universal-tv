package com.remote.control.allsmarttv.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.remote.control.allsmarttv.appUi.FirstActivity;
import com.remote.control.allsmarttv.utils.ir_utils.SupportedClass;
import com.remote.control.allsmarttv.databinding.ActivityPermissionBinding;

import java.util.ArrayList;

public class PermActivity extends AppCompatActivity {

    ActivityPermissionBinding activityPermBinding;
    String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportedClass.loadLangLocale(getBaseContext());
        activityPermBinding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(activityPermBinding.getRoot());

        activityPermBinding.btnPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validation();

            }
        });


    }

    private void validation() {

        Permissions.check(this, permission, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {


                Intent intent = new Intent(PermActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);

                validation();

            }
        });

    }
}