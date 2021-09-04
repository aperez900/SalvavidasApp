package com.desarollo.salvavidasapp.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desarollo.salvavidasapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class sellerStatistics extends Fragment {

    public sellerStatistics() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_statistics, container, false);

        //Barchart
        BarChart barChart = view.findViewById(R.id.barChartSeller);

        ArrayList<BarEntry> ventas = new ArrayList<>();
        ventas.add(new BarEntry(2017,420));
        ventas.add(new BarEntry(2018,475));
        ventas.add(new BarEntry(2019,500));
        ventas.add(new BarEntry(2020,520));
        ventas.add(new BarEntry(2021,550));

        BarDataSet barDataSet = new BarDataSet(ventas, "Ventas");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Gráfico de ejemplo");
        barChart.animateY(2000);

        //PieChart

        PieChart pieChart = view.findViewById(R.id.pieChartSeller);

        ArrayList<PieEntry> ventas2 = new ArrayList<>();
        ventas2.add(new PieEntry(500,"2017"));
        ventas2.add(new PieEntry(550,"2018"));
        ventas2.add(new PieEntry(610,"2019"));
        ventas2.add(new PieEntry(640,"2020"));
        ventas2.add(new PieEntry(700,"2021"));

        PieDataSet pieDataSet = new PieDataSet(ventas2,"Gráfico de ejemplo 2");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Ventas");
        pieChart.animate();

        return view;
    }
}