package com.example.root.musclediary;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

public class ListMuscles extends AppCompatActivity {

    /*
    * This activity is for the screen to select group of muscles
    *  and start loading data from shimmer
     */

    private String macAdd = "";

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    public TextView textConnect;
    MyGlobals aux = new MyGlobals();
    private String filename = "";
    Timer timer = new Timer();
    Button connectSensor = null;
    private boolean usesensor= true;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_muscles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List Muscles");

        Intent i = getIntent();
        usesensor = getIntent().getExtras().getBoolean("usesensor");
        connectSensor = (Button) findViewById(R.id.startRecord);
        pb = (ProgressBar) findViewById(R.id.progressconnection);
        if (usesensor) {
            //Loading progress bar dissapears when shimmer is ready to load data
            // sometimes connection is lost with shimmer, so user has to go back to
            // main screen and connect again. This has to be improved.
            aux = (MyGlobals) i.getSerializableExtra("primObject");
            pb.setVisibility(View.VISIBLE);
            connectSensor.setEnabled(false);
        }
        else {
            connectSensor.setAlpha(1);
            connectSensor.setBackgroundColor(getResources().getColor(R.color.colorButton));
        }

        //Spinner
        //List with group of muscles
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{ "Calf","Hamstrings", "Biceps","Triceps" };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        mDrawer = (DrawerLayout) findViewById(R.id.list_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(nvDrawer);

        macAdd = getIntent().getStringExtra("ITEM_EXTRA");
        filename = getIntent().getStringExtra("FILENAME");

        //Button to start loading data
        connectSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here we do the connection and the go to listmuscles

                Intent intent = new Intent(ListMuscles.this, LoadingScreen.class);
                if (usesensor) {
                    intent.putExtra("primObject", aux);
                    intent.putExtra("FILENAME", filename); //contains csv file to write shimmer sensor EMG data
                }
                intent.putExtra("usesensor", usesensor);

                ListMuscles.this.startActivity(intent);
            }
        });

        //This is to check connection with shimmer sensor
        // if state of connection is "Ready" then the progress bar dissappears
        // and start button is blue
        // Otherwise user has to go back and connect again
        if (usesensor) {
            timer.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              // use runOnUiThread(Runnable action)
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      if (aux != null) {
                                                          if (aux.getStateShimmer().equals("SD Logging")) {
                                                              connectSensor.setEnabled(true);
                                                              connectSensor.setAlpha(1);
                                                              connectSensor.setBackgroundColor(getResources().getColor(R.color.colorButton));
                                                              pb.setVisibility(View.GONE);
                                                              timer.cancel();
                                                          }
                                                          else if (aux.getStateShimmer().equals("Disconnected")) {
                                                              Toast.makeText(ListMuscles.this, "Error in connection, please go back and start again", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }
                                                  }
                                              });

                                          }
                                      },
                    0, 500);
        }
    }

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
    *MiHistory
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
        Intent intent = new Intent(ListMuscles.this, fragmentClass);
        ListMuscles.this.startActivity(intent);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.list_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        timer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        this.finish();
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

}
