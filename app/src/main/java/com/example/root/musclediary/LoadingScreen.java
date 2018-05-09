package com.example.root.musclediary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import android.widget.Toast;

import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.android.manager.ShimmerBluetoothManagerAndroid;
import com.shimmerresearch.bluetooth.ShimmerBluetooth;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driverUtilities.ChannelDetails;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.shimmerresearch.android.guiUtilities.ShimmerBluetoothDialog.EXTRA_DEVICE_ADDRESS;
import com.example.root.musclediary.MyGlobals;


public class LoadingScreen extends AppCompatActivity {

    /*
    * This activities loads the data and shows a Chronometer
     */

    ShimmerBluetoothManagerAndroid btManager;
    MyGlobals aux2 = new MyGlobals();
    private String filename = "";
    private boolean loadingscreen = true;

    //Chronometer
    Chronometer cmTimer;
    Button btnStart, btnStop;
    long elapsedTime;
    String TAG = "TAG";
    private boolean usesensor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Streaming");
        usesensor = getIntent().getExtras().getBoolean("usesensor");

        cmTimer = (Chronometer) findViewById(R.id.cmTimer);

        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) / 60;
                long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000) % 60;
                elapsedTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "onChronometerTick: " + minutes + " : " + seconds);
            }
        });

        //Start
        cmTimer.setBase(SystemClock.elapsedRealtime());
        cmTimer.start();

        //Start BTshimmer
        Intent i = getIntent();
        if (usesensor) {
            aux2 = (MyGlobals) i.getSerializableExtra("primObject");
            filename = getIntent().getStringExtra("FILENAME");
            aux2.startBT();
        }

        //Stop recording
        final Button connectSensor = (Button) findViewById(R.id.stopRecording);
        connectSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////
                cmTimer.stop();
                cmTimer.setText("");
                if (usesensor) {
                    aux2.stopBT(); //Stop loading data
                }

                //Go to resultscreen
                Intent intent = new Intent(LoadingScreen.this, ResultScreen.class);
                intent.putExtra("usesensor", usesensor);
                intent.putExtra("FILENAME", filename);
                intent.putExtra("loadingscreen",loadingscreen);
                LoadingScreen.this.startActivity(intent);
            }
        });
    }
}
