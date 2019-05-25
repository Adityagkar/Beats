package com.upgrad.beats;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        askforPermission();


    }
    private void askforPermission() {
        Dexter.withActivity(SplashScreen.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                SharedPreferences sharedPreferences = getSharedPreferences("MYSP",MODE_PRIVATE);
                                if (sharedPreferences.contains("USER")){
                                    //registered user
                                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(i);

                                }else{
                                    //new user
                                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                                    startActivity(i);

                                }


                                finish();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }


}

