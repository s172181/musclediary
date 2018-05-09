package com.example.root.musclediary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TabFragment2 extends Fragment {

    /*
    * This subfragment displays the barcharts of main amplitude EMG
    *  only for the calf muscle
    * Data here is static, the saving and extraction of data from db is
     * still to be implemented
     */

    BarChart chart;
    TextView legendx;

    float barWidth = 0.3f;
    float barSpace = 0f;
    float groupSpace = 0.4f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_fragment2, container, false);
        chart = (BarChart) v.findViewById(R.id.chart2);
        legendx  = (TextView) v.findViewById(R.id.graphbarlegend);



        final Button montlyb = (Button) v.findViewById(R.id.montlyb);
        final Button weeklyb = (Button) v.findViewById(R.id.weeklyb);
        final Button dailyb = (Button) v.findViewById(R.id.dailyb);

        /*
        * This displays the calendar
        * It's fake data so in this example when user selects 9 of may
        *  it goes to the result screen. This is to show an example of the functionallity.
        *  It has yet to be implemented.
         */
        final CalendarView calendarv = (CalendarView) v.findViewById(R.id.calendarView);
        dailyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarv.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);
                legendx.setVisibility(View.GONE);

                dailyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
            }
        });
        calendarv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_MONTH);
                int monOfWeek = calendar.get(Calendar.MONTH);
                if (dayOfMonth==9 && monOfWeek==4) {
                    Intent intent = new Intent(getActivity(), ResultScreen.class);
                    intent.putExtra("usesensor", false);
                    intent.putExtra("loadingscreen", false);
                    startActivity(intent);
                }
            }
        });

        weeklyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarv.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
                legendx.setVisibility(View.VISIBLE);

                dailyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));

                initBarchar(0);
            }
        });

        montlyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarv.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
                legendx.setVisibility(View.VISIBLE);

                dailyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));

                initBarchar(1);
            }
        });

        return v;
    }

    private void initBarchar(int type) {
        //Set data
        BarData data = new BarData(getBarData(type));
        chart.setData(data);

        //Yaxis left values
        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0);
        left.setAxisMaximum(12);
        left.setLabelCount(12);
        left.setDrawTopYLabelEntry(true);
        //Yaxis right values
        YAxis right = chart.getAxisRight();
        right.setDrawLabels(false);
        right.setDrawGridLines(false);
        right.setDrawZeroLine(true);
        right.setDrawTopYLabelEntry(true);

        //X axis
        XAxis xAxis = chart.getXAxis();
        final String[] labels = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        final String[] labels2 = {"","Mon","Tus","Wed","Thur","Fri","Sat","Sun"};

        if (type==0) {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels2));
            xAxis.setLabelCount(7);
        }
        else {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setLabelCount(12);
        }

        XAxis bottomAxis = chart.getXAxis();
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setDrawLabels(true);
        bottomAxis.setDrawGridLines(false);
        bottomAxis.setDrawAxisLine(true);

        //Chart values
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setClickable(false);
        chart.getLegend().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.setFitBars(true);

        chart.notifyDataSetChanged(); // let the chart know it's data changed
        chart.invalidate(); // refresh
    }

    private List<IBarDataSet> getBarData(int type){
        ArrayList<BarEntry> entries = new ArrayList<>();
        /*
        * This is fake data. Used to display an example of history screen.
        * It yet has to be implemented.
         */
        if (type==0) {
            entries.add(new BarEntry(1, (float) 1));
            entries.add(new BarEntry(2, (float) 2.5));
            entries.add(new BarEntry(3, (float) 5.1));
            entries.add(new BarEntry(4, (float) 4.3));
            entries.add(new BarEntry(5, (float) 2.1));
            entries.add(new BarEntry(6, (float) 4.3));
            entries.add(new BarEntry(7, (float) 6.9));

        }
        else {
            entries.add(new BarEntry(1, (float) 2.6));
            entries.add(new BarEntry(2, (float) 7.6));
            entries.add(new BarEntry(3, (float) 2.7));
            entries.add(new BarEntry(4, (float) 7.9));
            entries.add(new BarEntry(5, (float) 4.3));
            entries.add(new BarEntry(6, (float) 5.6));
            entries.add(new BarEntry(7, (float) 7.8));
            entries.add(new BarEntry(8, (float) 2.3));
            entries.add(new BarEntry(9, (float) 6.9));
            entries.add(new BarEntry(10, (float) 4.7));
            entries.add(new BarEntry(11, (float) 8.9));
            entries.add(new BarEntry(12, (float) 7.9));
        }

        List<IBarDataSet> bars = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "bar");
        dataSet.setColor(Color.parseColor("#f9b265"));
        dataSet.setHighlightEnabled(false);
        bars.add(dataSet);

        return bars;
    }

}
