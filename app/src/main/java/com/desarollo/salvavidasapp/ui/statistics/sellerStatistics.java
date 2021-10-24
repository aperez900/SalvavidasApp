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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class sellerStatistics extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores;
    ArrayList<BarEntry> ventas = new ArrayList<>();
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

        BarChart barChart = view.findViewById(R.id.barChartSeller);
        consultarDatos(barChart);

        return view;
    }

    private void consultarDatos(BarChart barChart) {

        myRefVendedores.child(currentUser.getUid()).child("productos_en_tramite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre las compras
                            for (DataSnapshot objsnapshot3 : objsnapshot2.getChildren()) { //recorre los productos
                                String diaSemana = Objects.requireNonNull(objsnapshot3.child("diaSemana").getValue()).toString();
                                String estado = Objects.requireNonNull(objsnapshot3.child("estado").getValue()).toString();
                                if (!estado.contains("Cancelado") && !estado.contains("Rechazado")  && !estado.contains("Anulado")){
                                    if(diaSemana.equals("Lunes") || diaSemana.contains("lun")){
                                        contadorLunes++;
                                    }
                                    if(diaSemana.equals("Martes") || diaSemana.contains("mar")){
                                        contadorMartes++;
                                    }
                                    if(diaSemana.equals("Miércoles") || diaSemana.contains("mié")){
                                        contadorMiercoles++;
                                    }
                                    if(diaSemana.equals("Jueves") || diaSemana.contains("jue")){
                                        contadorJueves++;
                                    }
                                    if(diaSemana.equals("Viernes") || diaSemana.contains("vie")){
                                        contadorViernes++;
                                    }
                                    if(diaSemana.equals("Sábado") || diaSemana.contains("sáb")){
                                        contadorSabado++;
                                    }
                                    if(diaSemana.equals("Domingo") || diaSemana.contains("dom")){
                                        contadorDomingo++;
                                    }
                                }
                            }
                        }
                    }
                    ventas.clear();
                    ventas.add(new BarEntry(1f,contadorLunes));
                    ventas.add(new BarEntry(2f,contadorMartes));
                    ventas.add(new BarEntry(3f,contadorMiercoles));
                    ventas.add(new BarEntry(4f,contadorJueves));
                    ventas.add(new BarEntry(5f,contadorViernes));
                    ventas.add(new BarEntry(6f,contadorSabado));
                    ventas.add(new BarEntry(7f,contadorDomingo));

                    mostrarTorta(barChart, ventas);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Aún no hay datos", Toast.LENGTH_SHORT).show();
                    barChart.clear();
                    barChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarTorta(BarChart barChart, ArrayList<BarEntry> ventas){

        BarDataSet barDataSet = new BarDataSet(ventas, "Ventas");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("");
        xLabels.add("Lunes");
        xLabels.add("Martes");
        xLabels.add("Miércoles");
        xLabels.add("Jueves");
        xLabels.add("Viernes");
        xLabels.add("Sábado");
        xLabels.add("Domingo");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setLabelCount(xLabels.size());

        barChart.setFitBars(true);
        barChart.clear();
        barChart.setData(barData);
        barChart.getDescription().setText("Diagrama de barras");
        barChart.animateY(1000);
        barChart.invalidate();
    }

}