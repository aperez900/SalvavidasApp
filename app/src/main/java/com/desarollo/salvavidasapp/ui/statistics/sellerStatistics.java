package com.desarollo.salvavidasapp.ui.statistics;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class sellerStatistics extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores;
    int contadorLunes=0;
    int contadorMartes=0;
    int contadorMiercoles=0;
    int contadorJueves=0;
    int contadorViernes=0;
    int contadorSabado=0;
    int contadorDomingo=0;

    public sellerStatistics() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");

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

        //PieChartSeller

        PieChart pieChart = view.findViewById(R.id.pieChartSeller);
        consultarDatos(pieChart);

        return view;
    }

    private void consultarDatos(PieChart pieChart) {

        myRefVendedores.child(currentUser.getUid()).child("productos_en_tramite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre las compras
                            for (DataSnapshot objsnapshot3 : objsnapshot2.getChildren()) { //recorre los productos
                                String diaSemana = objsnapshot3.child("diaSemana").getValue().toString();
                                if(diaSemana.equals("Lunes")){
                                    contadorLunes++;
                                }
                                if(diaSemana.equals("Martes")){
                                    contadorMartes++;
                                }
                                if(diaSemana.equals("Miércoles")){
                                    contadorMiercoles++;
                                }
                                if(diaSemana.equals("Jueves")){
                                    contadorJueves++;
                                }
                                if(diaSemana.equals("Viernes")){
                                    contadorViernes++;
                                }
                                if(diaSemana.equals("Sábado")){
                                    contadorSabado++;
                                }
                                if(diaSemana.equals("Domingo")){
                                    contadorDomingo++;
                                }
                            }
                        }
                    }
                    mostrarTorta(pieChart);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Aún no hay datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarTorta(PieChart pieChart){
        ArrayList<PieEntry> ventas2 = new ArrayList<>();
        ventas2.add(new PieEntry(contadorLunes,"Lunes"));
        ventas2.add(new PieEntry(contadorMartes,"Martes"));
        ventas2.add(new PieEntry(contadorMiercoles,"Miércoles"));
        ventas2.add(new PieEntry(contadorJueves,"Jueves"));
        ventas2.add(new PieEntry(contadorViernes,"Viernes"));
        ventas2.add(new PieEntry(contadorSabado,"Sábado"));
        ventas2.add(new PieEntry(contadorDomingo,"Domingo"));

        PieDataSet pieDataSet = new PieDataSet(ventas2,"Gráfico de ejemplo 2");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Ventas");
        pieChart.animate();
    }

}