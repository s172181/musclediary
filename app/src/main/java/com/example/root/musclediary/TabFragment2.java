package com.example.root.musclediary;

import android.content.Context;
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
import java.util.List;


public class TabFragment2 extends Fragment {

    BarChart chart;

    float barWidth = 0.3f;
    float barSpace = 0f;
    float groupSpace = 0.4f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_fragment2, container, false);
        chart = (BarChart) v.findViewById(R.id.chart2);



        final Button montlyb = (Button) v.findViewById(R.id.montlyb);
        final Button weeklyb = (Button) v.findViewById(R.id.weeklyb);
        final Button dailyb = (Button) v.findViewById(R.id.dailyb);
        final CalendarView calendar = (CalendarView) v.findViewById(R.id.calendarView);
        dailyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);

                dailyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
            }
        });

        weeklyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);

                dailyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));

                initBarchar(0);
            }
        });

        montlyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);

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
        left.setAxisMaximum(100);
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
        if (type==0) {
            entries.add(new BarEntry(1, (float) 1));
            entries.add(new BarEntry(2, (float) 11));
            entries.add(new BarEntry(3, (float) 7));
            entries.add(new BarEntry(4, (float) 16));
            entries.add(new BarEntry(5, (float) 35));
            entries.add(new BarEntry(6, (float) 21));
            entries.add(new BarEntry(7, (float) 40));

        }
        else {
            entries.add(new BarEntry(1, (float) 34));
            entries.add(new BarEntry(2, (float) 8));
            entries.add(new BarEntry(3, (float) 3));
            entries.add(new BarEntry(4, (float) 78));
            entries.add(new BarEntry(5, (float) 55));
            entries.add(new BarEntry(6, (float) 44));
            entries.add(new BarEntry(7, (float) 21));
            entries.add(new BarEntry(8, (float) 14));
            entries.add(new BarEntry(9, (float) 14));
            entries.add(new BarEntry(10, (float) 19));
            entries.add(new BarEntry(11, (float) 27));
            entries.add(new BarEntry(12, (float) 87));
        }

        List<IBarDataSet> bars = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "bar");
        dataSet.setColor(Color.parseColor("#f9b265"));
        dataSet.setHighlightEnabled(false);
        bars.add(dataSet);

        return bars;
    }

}
