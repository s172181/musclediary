package com.example.root.musclediary;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

public class ListMuscles extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_muscles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        Intent i = getIntent();
        aux = (MyGlobals) i.getSerializableExtra("primObject");

        //Spinner
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Bicepts", "Muscle2"};
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

        connectSensor = (Button) findViewById(R.id.startRecord);
        connectSensor.setEnabled(false);
        connectSensor.setBackgroundColor(0xFFFF0000);
        connectSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here we do the connection and the go to listmuscles

                Intent intent = new Intent(ListMuscles.this, LoadingScreen.class);
                intent.putExtra("primObject", aux);
                intent.putExtra("FILENAME", filename);

                ListMuscles.this.startActivity(intent);
            }
        });

        //Activate when state of shimmer is Login
        //Set the schedule function
        timer.scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                  // use runOnUiThread(Runnable action)
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          // Magic here
                          //connectSensor.setEnabled(true);
                          if (aux!=null) {
                              if (aux.getStateShimmer().equals("SD Logging")) {
                                  connectSensor.setEnabled(true);
                                  connectSensor.setBackgroundColor(getResources().getColor(R.color.colorButton));
                                  timer.cancel();
                              }
                          }
                      }
                  });

                  }
              },
        0, 500);
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

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_hist:
                fragmentClass = HistoryFragment.class;
                break;
            case R.id.nav_exer:
                fragmentClass = HistoryFragment.class;
                break;
            case R.id.nav_feed:
                fragmentClass = HistoryFragment.class;
                break;
            default:
                fragmentClass = HistoryFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

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
