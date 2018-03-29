package com.example.root.musclediary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;

public class ResultScreen extends AppCompatActivity {

    int startofcsv = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        List<Float> xvalues = new ArrayList<Float>();
        List<Float> yvalues = new ArrayList<Float>();
        float fvalue = 0;
        float newv = 0;
        int it = 0;
        float peakvalue = 0;

        //Read from file
        try{
            final InputStream in = getAssets().open( "20180321150413calf.dat" );
            InputStreamReader csvStreamReader = new        InputStreamReader(in);
            CSVReader reader = new CSVReader(csvStreamReader, '\t');
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (it>=startofcsv) {
                    if (it == startofcsv) {
                        fvalue = Float.parseFloat(nextLine[4]);
                    }
                    //Getting max value
                    if (Float.parseFloat(nextLine[0])>peakvalue)
                        peakvalue = Float.parseFloat(nextLine[0]);
                    newv = Float.parseFloat(nextLine[4]) - fvalue;
                    xvalues.add(newv);
                    yvalues.add(Float.parseFloat(nextLine[0]));
                    if (it == 100)
                        break;
                    //System.out.println(it+" Test " + nextLine[0]+" -- "+newv + "--" + nextLine[4] + " etc...");
                }
                it++;
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Graph here
        //y the times
        //x the averages
        DataPoint[] dataPoints = new DataPoint[xvalues.size()]; // declare an array of DataPoint objects with the same size as your list
        for (int i = 0; i < xvalues.size(); i++) {
            // add new DataPoint object to the array for each of your list entries
            dataPoints[i] = new DataPoint(xvalues.get(i),yvalues.get(i)); // not sure but I think the second argument should be of type double
        }
        GraphView graphview = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graphview.addSeries(series);

        // activate horizontal zooming and scrolling
        graphview.getViewport().setScalable(true);

        // activate horizontal scrolling
        graphview.getViewport().setScrollable(true);

        // set manual X bounds
        graphview.getViewport().setXAxisBoundsManual(true);
        graphview.getViewport().setMinX(0.5);
        graphview.getViewport().setMaxX(3.5);

        //Progress bar
        TextView barnum = (TextView) findViewById(R.id.peek);
        barnum.setText("Peek value: "+peakvalue);
        //BootstrapProgressBar bar = (BootstrapProgressBar) findViewById(R.id.peekvalue);
        //bar.setProgress(peakvalue);
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ResultScreen.this, ListMuscles.class);
        startActivity(intent);
    }

}
