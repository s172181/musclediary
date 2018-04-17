package com.example.root.musclediary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;

import java.util.Date;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;

import org.apache.commons.lang3.ArrayUtils;

public class ResultScreen extends AppCompatActivity {

    int startofcsv = 4;

    FrameLayout progressBarHolder;
    ProgressBar progressBarStyleLarge;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    boolean loadinggraph = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBarStyleLarge = (ProgressBar) findViewById(R.id.progressBarimg);
        new MyTask().execute();

        //Wait 2 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                List<Float> xvalues = new ArrayList<Float>();
                List<Float> xvaluesS = new ArrayList<Float>();
                List<Float> yvalues = new ArrayList<Float>();
                float totalminutes = 0;
                float previousminutes = 0;
                float fvalue = 0;
                float newv = 0;
                int it = 0;
                float peakvalue = 0;
                int Active=0, Inactive=0, Sum=0;
                double rate=0;
                double avg = 0;
                SimpleMovingAverage currAvg = new SimpleMovingAverage(100);

                //Read from file
                try {
                    final InputStream in = getAssets().open("20180321150413calf.dat");
                    InputStreamReader csvStreamReader = new InputStreamReader(in);
                    CSVReader reader = new CSVReader(csvStreamReader, '\t');

                    /*String uri = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                    String fileName = getIntent().getStringExtra("FILENAME");

                    uri=uri + File.separator + fileName;
                    File file = new File(uri);
                    System.out.println("ManualDeb: filename "+uri);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader csvStreamReader = new InputStreamReader(fileInputStream);
                    CSVReader reader = new CSVReader(csvStreamReader, ',');*/
                    String[] nextLine;
                    float timestamp = 0;
                    float emg = 0;
                    while ((nextLine = reader.readNext()) != null) {
                        if (it >= startofcsv) {
                            /*timestamp = Float.parseFloat(nextLine[0]);
                            emg = Float.parseFloat(nextLine[2]);*/
                            timestamp = Float.parseFloat(nextLine[4]);
                            emg = Float.parseFloat(nextLine[0]);
                            if (it == startofcsv) {
                                fvalue = timestamp;
                            }
                            //Getting max value
                            if (emg > peakvalue)
                                peakvalue = emg;
                            if (timestamp>previousminutes)
                                newv = timestamp - fvalue;
                            else {
                                it++;
                                continue;
                            }
                            previousminutes = newv;

                            xvalues.add(newv);
                            xvaluesS.add(newv/60000); //time in minutes
                            totalminutes = newv/60000; //time in minutes
                            currAvg.addData(emg);
                            avg = currAvg.getMean();
                            yvalues.add((float) avg);
                    /*if (it == 500000)
                        break;*/
                        }
                        it++;
                    }
                } catch (Exception e) {
                    loadinggraph = false;
                    e.printStackTrace();
                    System.out.println("ManualDeb: filename error "+e.getMessage());
                    Toast.makeText(ResultScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                
                SimpleMovingAverage TrainingAvgObj = new SimpleMovingAverage(it);
                SimpleMovingAverage ChangeRate = new SimpleMovingAverage(1000);


                for (float i:yvalues) {
                    TrainingAvgObj.addData(i);
                    ChangeRate.addData(i);
                    if(ChangeRate.getChange() > 0.3 || ChangeRate.getChange() < -0.3) {
                        Active++;
                    }
                    else
                    {
                    Inactive++;
                    }
                }
                Sum = Active + Inactive;
                rate= (double)Active*100000/(double)Sum;

                //Graph here
                //y the times
                //x the averages
                DataPoint[] dataPoints = new DataPoint[xvalues.size()]; // declare an array of DataPoint objects with the same size as your list
                for (int i = 0; i < xvalues.size(); i++) {
                    // add new DataPoint object to the array for each of your list entries
                    dataPoints[i] = new DataPoint(xvaluesS.get(i), yvalues.get(i)); // not sure but I think the second argument should be of type double
                    
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
                graphview.getViewport().setMinX(0);

                int totalminutes2 = 3;
                if (totalminutes>10)
                    totalminutes2 = Math.round(totalminutes/2);
                else if (totalminutes>1)
                    totalminutes2 = Math.round(totalminutes);
                else if (totalminutes<=1)
                    totalminutes2 = Math.round(1);

                System.out.println("ManualDeb: Total minutes: "+totalminutes2);
                graphview.getViewport().setMaxX(totalminutes2+0.5);

                //Progress bar
                TextView barnum = (TextView) findViewById(R.id.peek);
                barnum.setText("Peek value: " + peakvalue);

                TextView barnum1 = (TextView) findViewById(R.id.average);
                barnum1.setText("Average value: " + TrainingAvgObj.getMean());

                TextView barnum2 = (TextView) findViewById(R.id.active);
                barnum2.setText("Active training in %: " + Math.round(rate));
                loadinggraph = false;
                //BootstrapProgressBar bar = (BootstrapProgressBar) findViewById(R.id.peekvalue);
                //bar.setProgress(peakvalue);
            }
            }, 1000);   //2 seconds
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ResultScreen.this, ListMuscles.class);
        startActivity(intent);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setAlpha(1);
            progressBarHolder.setBackgroundColor(0xFFFFFFFF);
            progressBarStyleLarge.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (loadinggraph) {
                    //System.out.println("ManualDeb: Emulating some task.. Step ");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void simplifyGraph(CSVReader reader) throws IOException {
        List<Float> xvalues = new ArrayList<Float>();
        List<Float> yvalues = new ArrayList<Float>();
        float fvalue = 0;
        float newv = 0;
        int it = 0;
        float peakvalue = 0;

        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (it >= startofcsv) {
                if (it == startofcsv) {
                    fvalue = Float.parseFloat(nextLine[4]);
                }


                if (it == 500000)
                    break;
                //System.out.println(it+" Test " + nextLine[0]+" -- "+newv + "--" + nextLine[4] + " etc...");
            }
            it++;
        }

    }


    // Java program to calculate
// Simple Moving Average

    public class SimpleMovingAverage {

        // queue used to store list so that we get the average
        private final Queue<Double> Dataset = new LinkedList<Double>();
        private final int period;
        private double sum;
        double valuechange= 0;
        int counter=0;
        // constructor to initialize period
        public SimpleMovingAverage(int period) {
            this.period = period;
            counter = period;
        }

        // function to add new data in the
        // list and update the sum so that
        // we get the new mean
        public void addData(double num) {
            sum += num;
            Dataset.add(num);


            // Updating size so that length
            // of data set should be equal
            // to period as a normal mean has
            if (Dataset.size() > period) {
                sum -= Dataset.remove();
                if(counter==0) counter = period;
                counter--;
            }
        }

        // function to calculate mean
        public double getMean() {
            return sum / period;
        }

        public double getChange() {
            if(counter==0) {
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                List array = new ArrayList(Dataset);
                min = (double) Collections.min(array);
                max = (double) Collections.max(array);
                valuechange = max - min;
                return valuechange;
            }
            else  return  0;
        }



    }
}