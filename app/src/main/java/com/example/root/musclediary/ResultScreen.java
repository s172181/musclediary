package com.example.root.musclediary;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.TestLooperManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;


import org.apache.commons.lang3.ArrayUtils;

public class ResultScreen extends AppCompatActivity {

    /*
    * This activity displays result Screen
    *  with EMG graph and the values "Mean amplitude", "Maximum amplitude", "Active training"
     */

    //Start of the file, normally the file from shimmercapture starts with data at the 4 row
    int startofcsv = 2;

    FrameLayout progressBarHolder;
    ProgressBar progressBarStyleLarge;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    private boolean loadingscreen = false;

    boolean loadinggraph = true;
    private boolean usesensor = true;
    LineChart chart;
    TextView zoomtext;

    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        zoomtext = (TextView) findViewById(R.id.zoomin0);

        //Create database
        // Get singleton instance of database
        // this is to save results. To be implemented.
        /*DBHelper databaseHelper = DBHelper.getInstance(this);
        databaseHelper.insertContent("0.5",0.6,0.7);
        databaseHelper.getAllPosts();*/

        TextView ttitle1 = (TextView) findViewById(R.id.title1);
        ttitle1.setTypeface(null, Typeface.BOLD);
        TextView ttitle2 = (TextView) findViewById(R.id.title2);
        ttitle2.setTypeface(null, Typeface.BOLD);

        usesensor = getIntent().getExtras().getBoolean("usesensor");
        loadingscreen = getIntent().getExtras().getBoolean("loadingscreen");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result Screen");

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
                long minutes = 0;
                SimpleMovingAverage currAvg = new SimpleMovingAverage(100);
                //SimpleMovingAverage currAvg = new SimpleMovingAverage(1);

                //Read from file
                try {
                    //Here we read from the file
                    //Change name of the file here
                    CSVReader reader;
                    /*
                    * If user selected "skip" on the first screen, sample data will be loaded
                    * and displayed
                    * Otherwise data extracted from shimmer sensor will be displayed
                    *  obtained from the csv file created in MainActivity and loaded in LoadingScreen
                     */
                    if (!usesensor) {
                        final InputStream in = getAssets().open("sample1.csv");
                        //final InputStream in = getAssets().open("sample2.csv");
                        InputStreamReader csvStreamReader = new InputStreamReader(in);
                        reader = new CSVReader(csvStreamReader, ',');
                    }
                    else {
                        String uri = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = getIntent().getStringExtra("FILENAME");

                        uri = uri + File.separator + fileName;
                        File file = new File(uri);
                        System.out.println("ManualDeb: filename " + uri);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        InputStreamReader csvStreamReader = new InputStreamReader(fileInputStream);
                        reader = new CSVReader(csvStreamReader, ',');
                    }
                    String[] nextLine;
                    float timestamp = 0;
                    float emg = 0;
                    float seconds = 0;
                    while ((nextLine = reader.readNext()) != null) {
                        if (it >= startofcsv) {
                            timestamp = Float.parseFloat(nextLine[0]);
                            emg = Float.parseFloat(nextLine[2]); //Channel 1
                            //Here is the columns were we read from the file
                            //since we are using channel one then we need to select column
                            //column 1 (EMG_CH1_24BIT)
                            //column 0 Timestamp

                            if (it == startofcsv) {
                                fvalue = timestamp;
                            }
                            if (timestamp>previousminutes)
                                newv = timestamp - fvalue;
                            else {
                                it++;
                                continue;
                            }
                            previousminutes = newv;
                            //Getting max value
                            //This value is not used anymore
                            if (emg > peakvalue)
                                peakvalue = emg;

                            xvalues.add(newv);
                            currAvg.addData(emg);
                            avg = currAvg.getMean();
                            yvalues.add((float) avg);
                        }
                        it++;
                    }
                } catch (Exception e) {
                    loadinggraph = false;
                    e.printStackTrace();
                    System.out.println("ManualDeb: filename error "+e.getMessage());
                    //Toast.makeText(ResultScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //Getting emg average

                }

                SimpleMovingAverage TrainingAvgObj = new SimpleMovingAverage(it);
                SimpleMovingAverage ChangeRate = new SimpleMovingAverage(1000);


                ArrayList<Double> peaks = new ArrayList<Double>();

                /*
                *Here we get active training, maximum amplitude and mean amplitude
                 */
                for (float i:yvalues) {
                    TrainingAvgObj.addData(i);
                    ChangeRate.addData(i);
                    peaks.add(ChangeRate.getChange());
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

                //Create Graph here
                //y the times
                //x the averages
                DataPoint[] dataPoints = new DataPoint[xvalues.size()];
                List<Entry> entries = new ArrayList<Entry>();
                for (int i = 0; i < xvalues.size(); i++) {
                    dataPoints[i] = new DataPoint(xvalues.get(i), yvalues.get(i));
                    entries.add(new Entry(xvalues.get(i), yvalues.get(i)));
                }

                chart = (LineChart) findViewById(R.id.graph);
                LineDataSet dataSet = new LineDataSet(entries, null);
                chart.getLegend().setEnabled(false);
                dataSet.setLineWidth(1f);
                dataSet.setColor(Color.parseColor("#3953c7"));
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);

                LineData lineData = new LineData(dataSet);
                lineData.setValueFormatter(new MyValueFormatter());

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IAxisValueFormatter()
                { @Override
                public String getFormattedValue(float value, AxisBase axis)
                {
                    String val1 = String.format("%d", TimeUnit.MILLISECONDS.toMinutes((long) value)
                    );
                    String val3 = String.format("%d", TimeUnit.MILLISECONDS.toSeconds((long) value) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) value))
                    );
                    return val1+":"+val3;
                }
                });
                YAxis right = chart.getAxisRight();
                right.setDrawLabels(false);

                chart.setData(lineData);
                chart.getDescription().setEnabled(false);
                chart.invalidate();

                /*
                * Efficiency results:
                 */
                TextView barnum = (TextView) findViewById(R.id.peek);
                barnum.setText("Maximum amplitude: " + String.format("%.2f", Collections.max(peaks)));

                TextView barnum1 = (TextView) findViewById(R.id.average);
                //barnum1.setText("Average value: " + TrainingAvgObj.getMean());
                barnum1.setText("Mean amplitude " + String.format("%.2f", ChangeRate.getAverageEMG()));

                TextView barnum2 = (TextView) findViewById(R.id.active);
                barnum2.setText("Active training: " + Math.round(rate)+" %");

                loadinggraph = false;

            }
        }, 1000);   //2 seconds

        ImageView infoactive = findViewById(R.id.infoev2);
        infoactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ResultScreen.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ResultScreen.this);
                }
                builder.setIcon(R.drawable.iconchart);
                builder.setTitle("Active training")
                        .setMessage("It shows fraction of the training time that muscle was kept active \n\n")
                        .setPositiveButton("OK",null)
                        .setIcon(R.drawable.iconchart)
                        .show();
            }
        });
        ImageView infoactive2 = findViewById(R.id.infoev3);
        infoactive2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ResultScreen.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ResultScreen.this);
                }
                builder.setIcon(R.drawable.iconchart);
                builder.setTitle("Maximum amplitude")
                        .setMessage("Shows peak amplitude of EMG within training that represents the most significant repetition\n\n")
                        .setPositiveButton("OK",null)
                        .setIcon(R.drawable.iconchart)
                        .show();
            }
        });
        ImageView infoactive3 = findViewById(R.id.infoev4);
        infoactive3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ResultScreen.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ResultScreen.this);
                }
                builder.setIcon(R.drawable.iconchart);
                builder.setTitle("Mean amplitude")
                        .setMessage("Represents average of all amplitudes within a training, that corresponds to the real efficiency of the training\n\n")
                        .setPositiveButton("OK",null)
                        .setIcon(R.drawable.iconchart)
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
        if (loadingscreen) {
            Intent intent = new Intent(ResultScreen.this, ListMuscles.class);
            intent.putExtra("usesensor", usesensor);
            startActivity(intent);
        }

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
            LinearLayout progressBarsvalue = (LinearLayout) findViewById(R.id.progressBarsvalue);
            progressBarsvalue.setVisibility(View.VISIBLE);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setAlpha(1);
            progressBarHolder.setBackgroundColor(0xFFFFFFFF);
            progressBarStyleLarge.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
            zoomtext.setVisibility(View.VISIBLE);
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

    /*
    Not used
     */
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


    /*
    * Class to make calculations about active training,
    * maximum amplitude and mean amplitude
     */
    public class SimpleMovingAverage {

        // queue used to store list so that we get the average
        private final Queue<Double> Dataset = new LinkedList<Double>();
        private final int period;
        private double sum;
        double valuechange= 0;
        int counter=0;
        //Average EMG 2
        double averEMG = 0;
        int numberPeriods = 0;

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

        public double getAverageEMG() { System.out.println("ManualDeb: "+averEMG+" "+numberPeriods);
            return averEMG/numberPeriods;}

        public double getChange() {
            if(counter==0) {
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                List array = new ArrayList(Dataset);
                min = (double) Collections.min(array);
                max = (double) Collections.max(array);
                valuechange = max - min;
                averEMG += valuechange;
                numberPeriods++;
                return valuechange;
            }
            else  return  0;
        }



    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }
}