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

import com.github.mikephil.charting.charts.PieChart;
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
import java.util.Objects;

public class buyerStatistics extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios;
    ArrayList<PieEntry> ventas2 = new ArrayList<>();

    public buyerStatistics() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefUsuarios = database.getReference("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        //Barchart
        PieChart pieChart = view.findViewById(R.id.pieChart);

        consultarDatos(pieChart);

        return view;
    }

    private void consultarDatos(PieChart pieChart) {

        myRefUsuarios.child(currentUser.getUid()).child("mis_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> ListNombres = new ArrayList<>();
                    ventas2.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre las compras
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                            String estado = Objects.requireNonNull(objsnapshot2.child("estado").getValue()).toString();
                            if (!estado.contains("Cancelado") && !estado.contains("Rechazado")
                                    && !estado.contains("Anulado")) {
                                String nombreProducto = Objects.requireNonNull(objsnapshot2.child("nombreProducto")
                                        .getValue()).toString();
                                ListNombres.add(nombreProducto);
                            }
                        }
                    }
                    contarProducto(ListNombres,pieChart);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Aún no hay datos", Toast.LENGTH_SHORT).show();
                    pieChart.clear();
                    pieChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void contarProducto(ArrayList<String> ListNombres, PieChart pieChart){
        int contador;
        for (int i=0; i < ListNombres.size(); i++){
            String nombreABuscar = ListNombres.get(i);
            contador=0;
            for (String nombre : ListNombres) {
                if (nombre.equals(nombreABuscar)) {
                    contador++;
                }
            }
            i++;
            ventas2.add(new PieEntry(contador,nombreABuscar));
        }
        mostrarTorta(pieChart, ventas2);
    }

    /*
    public void mostrarBarras(BarChart barChart, ArrayList<BarEntry> ventas, ArrayList<String> xLabels){

        BarDataSet barDataSet = new BarDataSet(ventas, "Ventas");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

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
     */

    public void mostrarTorta(PieChart pieChart, ArrayList<PieEntry> ventas2){

        PieDataSet pieDataSet = new PieDataSet(ventas2,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.clear();
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Ventas");
        pieChart.animate();
        pieChart.invalidate();
    }
}