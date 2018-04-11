package com.example.root.musclediary;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class LoadingScreen extends AppCompatActivity {
    Chronometer cmTimer;
    Button btnStart, btnStop;
    long elapsedTime;
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cmTimer = (Chronometer) findViewById(R.id.cmTimer);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);

        // example setOnChronometerTickListener
        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) / 60;
                long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) % 60;
                elapsedTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "onChronometerTick: " + minutes + " : " + seconds);
            }
        });


        final Button connectSensor = (Button) findViewById(R.id.btnStop);
        connectSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here we do the connection and the go to listmuscles
                Intent intent = new Intent(LoadingScreen.this, ResultScreen.class);
                LoadingScreen.this.startActivity(intent);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                cmTimer.stop();
                cmTimer.setText("");
            }
        });

        final Button connectSensorStart = (Button) findViewById(R.id.btnStart);
        connectSensorStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                cmTimer.setBase(SystemClock.elapsedRealtime());
                cmTimer.start();
            }
        });

        //Recording info maybe?
    }
}
