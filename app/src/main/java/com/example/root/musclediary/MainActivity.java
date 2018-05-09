package com.example.root.musclediary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shimmerresearch.android.Shimmer;
import com.shimmerresearch.android.guiUtilities.ShimmerBluetoothDialog;
import com.shimmerresearch.android.manager.ShimmerBluetoothManagerAndroid;
import com.shimmerresearch.bluetooth.ShimmerBluetooth;
import com.shimmerresearch.driver.CallbackObject;
import com.shimmerresearch.driver.Configuration;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.ShimmerDevice;
import com.shimmerresearch.driverUtilities.ChannelDetails;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import com.example.root.musclediary.MyGlobals;

import static com.shimmerresearch.android.guiUtilities.ShimmerBluetoothDialog.EXTRA_DEVICE_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    public static final int PERMISSION_ASK = 1001;
    MyGlobals aux = new MyGlobals();
    private final static int PERMISSIONS_REQUEST_WRITE_STORAGE = 5;
    private final static String LOG_TAG = "ObjectClusterExample";

    //Write to CSV variables
    private FileWriter fw;
    private BufferedWriter bw;
    private File file;
    private String fileName = "";
    private boolean usesensor = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mucle Diary");

        /*
        * This is to use check if user wants to connect to sensor and load data from it
        *  or skip this to be able to navigate to result screen where sample data
        *  will be shown
         */
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle("Shimmer Sensor")
                .setMessage("Do you want to use a Shimmer sensor or skip this to navigate the app?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .setNegativeButton("skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        usesensor = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        if (usesensor)
            aux.setBTHandler(this,mHandler);

        //Check if permission to write to external storage has been granted
        //This is to write the csv file into the external storage
        if (Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }

        //Button to connect to shimmer
        final Button connectSensor = (Button) findViewById(R.id.buttonConnect);
        connectSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usesensor)
                    startbluet();
                else {
                    Intent intent = new Intent(MainActivity.this, ListMuscles.class);
                    intent.putExtra("usesensor", usesensor);
                    MainActivity.this.startActivity(intent);
                }
            }
        });

        //This is for the menu
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);

        //Setup CSV writing
        if (usesensor) {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName = "EMGData" + DateFormat.getDateTimeInstance().format(new Date()) + ".csv";
            String filePath = baseDir + File.separator + fileName;
            file = new File(filePath);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the result from the paired devices dialog
     *  Here after connected to the shimmer via bluetooth it goes to the ListMuscle screens
     *   and add the  object that contains ShimmerBluetoothManagerAndroid to the intent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (usesensor) {
            if (requestCode == 2) {
                if (resultCode == Activity.RESULT_OK) {
                    //Get the Bluetooth mac address of the selected device:
                    String macAdd = data.getStringExtra(EXTRA_DEVICE_ADDRESS);
                    aux.setBT(macAdd);
                    //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, ListMuscles.class);
                    intent.putExtra("primObject", aux);
                    intent.putExtra("usesensor", usesensor);
                    intent.putExtra("FILENAME", fileName); //Get the Bluetooth mac address of the selected device
                    MainActivity.this.startActivity(intent);
                } else
                    Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    * Function to start connection via bluetooth
    *  using the shimmer API
     */
    public void startbluet() {
        //Shimmer uses this
        //btManager.disconnectAllDevices();   //Disconnect all devices first
        Intent intent = new Intent(getApplicationContext(), ShimmerBluetoothDialog.class);
        startActivityForResult(intent, ShimmerBluetoothDialog.REQUEST_CONNECT_SHIMMER);
    }

    /*
    * Menu
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /*
    * Menu
     */
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_hist:
                fragmentClass = MyHistory.class;
                break;
            default:
                fragmentClass = MyHistory.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        Intent intent = new Intent(MainActivity.this, fragmentClass);
        MainActivity.this.startActivity(intent);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Permission request callback for writing storage
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Error! Permission not granted. App will now close", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(null);
        aux = new MyGlobals();
        aux.setBTHandler(this,null);
        aux.setBTHandler(this,mHandler);
    }


    /*
    * This handler is stracted from the API Shimmer examples and slightly modified
    *  this handles the communication with shimmer and extraction of data
    *   it starts from MainScreen and its bound to it, so it runs in background
     */
    //android.os.Handler allows us to send and process Message and Runnable
    // objects associated with a thread's MessageQueue. Each Handler
    // instance is associated with a single thread and that thread's message queue.
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ShimmerBluetooth.MSG_IDENTIFIER_DATA_PACKET:
                    if ((msg.obj instanceof ObjectCluster)) {
                        ObjectCluster objc = (ObjectCluster) msg.obj;

                        /**
                         * ---------- Printing a channel to Logcat ----------
                         */
                        //Method 1 - retrieve data from the ObjectCluster using get method
                        double data = objc.getFormatClusterValue(Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X, ChannelDetails.CHANNEL_TYPE.CAL.toString());
                        Log.i(LOG_TAG, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X + " data: " + data);

                        //Method 2a - retrieve data from the ObjectCluster by manually parsing the arrays
                        int index = -1;
                        for(int i=0; i<objc.sensorDataArray.mSensorNames.length; i++) {
                            if(objc.sensorDataArray.mSensorNames[i] != null) {
                                if (objc.sensorDataArray.mSensorNames[i].equals(Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X)) {
                                    index = i;
                                }
                            }
                        }
                        if(index != -1) {
                            //Index was found
                            data = objc.sensorDataArray.mCalData[index];
                            Log.w(LOG_TAG, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X + " data: " + data);
                        }

                        //Method 2b - retrieve data from the ObjectCluster by getting the index, then accessing the arrays
                        index = objc.getIndexForChannelName(Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X);
                        if(index != -1) {
                            data = objc.sensorDataArray.mCalData[index];
                            Log.e(LOG_TAG, Configuration.Shimmer3.ObjectClusterSensorName.ACCEL_WR_X + " data: " + data);
                        }

                        /**
                         * ---------- Writing all channels of CAL data to CSV file ----------
                         */
                        if(aux.getfirstTimeWrite()) {
                            //Write headers on first-time
                            for(String channelName : objc.sensorDataArray.mSensorNames) {
                                try {
                                    bw.write(channelName + ",");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                bw.write("\n");
                            } catch(IOException e2) {
                                e2.printStackTrace();
                            }
                            aux.setfirstTimeWrite(false);
                        }
                        for(double calData : objc.sensorDataArray.mCalData) {
                            String dataString = String.valueOf(calData);
                            try {
                                bw.write(dataString + ",");
                            } catch(IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        try {
                            bw.write("\n");
                        } catch(IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    break;
                case ShimmerBluetooth.MSG_IDENTIFIER_STATE_CHANGE:
                    ShimmerBluetooth.BT_STATE state = null;
                    String macAddress = "";

                    if (msg.obj instanceof ObjectCluster) {
                        state = ((ObjectCluster) msg.obj).mState;
                        macAddress = ((ObjectCluster) msg.obj).getMacAddress();
                    } else if (msg.obj instanceof CallbackObject) {
                        state = ((CallbackObject) msg.obj).mState;
                        macAddress = ((CallbackObject) msg.obj).mBluetoothAddress;
                    }
                    System.out.println("ManualDeb: State:..."+state.toString());
                    aux.setState(state.toString());
                    switch (state) {
                        case CONNECTED:
                            break;
                        case CONNECTING:
                            break;
                        case STREAMING:
                            break;
                        case STREAMING_AND_SDLOGGING:
                            break;
                        case SDLOGGING:
                            break;
                        case DISCONNECTED:
                            break;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
