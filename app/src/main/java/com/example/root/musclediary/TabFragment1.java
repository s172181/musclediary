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
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class TabFragment1 extends Fragment {

    /*
    * This subfragment displays the piechart of percentage of training
    * muscles
    * Data here is static, the saving and extraction of data from db is
     * still to be implemented
     */

    PieChart pieChart ;
    ArrayList<PieEntry> entries ;
    ArrayList<PieEntry> entries2 ;

    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_fragment1, container, false);
        //mainLayout = (RelativeLayout) v.findViewById(R.id.mainLayout);

        //Weekly
        pieChart = (PieChart) v.findViewById(R.id.chart1);
        pieChart.getDescription().setEnabled(false);
        entries = new ArrayList<PieEntry>();
        PieEntryLabels = new ArrayList<String>();
        AddValuesToPIEENTRY(1);

        int colorWhite = Color.parseColor("#ffffff");
        pieChart.setDrawEntryLabels(false);

        pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setValueTextSize(18f);
        pieDataSet.setValueTextColor(colorWhite);
        pieData = new PieData(pieDataSet);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#f3b6ac"));
        colors.add(Color.parseColor("#ea673e"));
        colors.add(Color.parseColor("#f94800"));
        colors.add(Color.parseColor("#ffaa4e"));
        pieDataSet.setColors(colors);
        pieChart.setData(pieData);
        pieChart.animateY(0);

        final Button montlyb = (Button) v.findViewById(R.id.montlyb);
        final Button weeklyb = (Button) v.findViewById(R.id.weeklyb);
        montlyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));

                AddValuesToPIEENTRY(2);
                pieDataSet = new PieDataSet(entries2, "");
                pieDataSet.setValueTextSize(18f);
                pieDataSet.setValueTextColor(Color.parseColor("#ffffff"));
                pieData = new PieData(pieDataSet);
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#f3b6ac"));
                colors.add(Color.parseColor("#ea673e"));
                colors.add(Color.parseColor("#f94800"));
                colors.add(Color.parseColor("#ffaa4e"));
                pieDataSet.setColors(colors);
                pieChart.setData(pieData);
                pieChart.notifyDataSetChanged(); // let the chart know it's data changed
                pieChart.invalidate(); // refresh
            }
        });

        weeklyb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weeklyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border));
                montlyb.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_border2));

                AddValuesToPIEENTRY(1);
                pieDataSet = new PieDataSet(entries, "");
                pieDataSet.setValueTextSize(18f);
                pieDataSet.setValueTextColor(Color.parseColor("#ffffff"));
                pieData = new PieData(pieDataSet);
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#f3b6ac"));
                colors.add(Color.parseColor("#ea673e"));
                colors.add(Color.parseColor("#f94800"));
                colors.add(Color.parseColor("#ffaa4e"));
                pieDataSet.setColors(colors);
                pieChart.setData(pieData);
                pieChart.notifyDataSetChanged(); // let the chart know it's data changed
                pieChart.invalidate(); // refresh
            }
        });

        return v;
    }

    public void AddValuesToPIEENTRY(int type){

        /*
        * This is fake data. Used to display an example of history screen.
        * It yet has to be implemented.
         */
        if (type==1) {
            entries = new ArrayList<PieEntry>();
            entries.add(new PieEntry(18.5f, "Upper Leg"));
            entries.add(new PieEntry(26.7f, "Low leg"));
            entries.add(new PieEntry(24.0f, "Biceps"));
            entries.add(new PieEntry(30.8f, "Triceps"));
        }
        else {
            entries2 = new ArrayList<PieEntry>();
            entries2.add(new PieEntry(20f, "Upper Leg"));
            entries2.add(new PieEntry(20f, "Low leg"));
            entries2.add(new PieEntry(20f, "Biceps"));
            entries2.add(new PieEntry(40f, "Triceps"));
        }

    }

    public void AddValuesToPieEntryLabels(){

        PieEntryLabels.add("January");
        PieEntryLabels.add("February");
        PieEntryLabels.add("March");
        PieEntryLabels.add("April");
        PieEntryLabels.add("May");
        PieEntryLabels.add("June");

    }

}
