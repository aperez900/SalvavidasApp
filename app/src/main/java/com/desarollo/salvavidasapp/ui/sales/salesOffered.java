package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class
salesOffered extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSaleAdapter listSaleAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView titulo_ofertados, subtitulo_ofertados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_offered);

        titulo_ofertados = findViewById(R.id.tv_titulo_ofertados);
        subtitulo_ofertados = findViewById(R.id.tv_subtitulo_ofertados);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoOfertados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        listSaleAdapter = new ListSaleAdapter(this, listaDeDatos,this);
        listado.setAdapter(listSaleAdapter);

        crearListado();
    }

    private void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        Productos p = objsnapshot.getValue(Productos.class);

                        String estado="";

                        Date fechaInicio = null;
                        Date fechaFin = null;
                        Date getCurrentDateTime = null;
                        try {
                            fechaInicio = sdf.parse(p.getFechaInicio() + " " + p.getHoraInicio());
                            fechaFin = sdf.parse(p.getFechaFin() + " " + p.getHoraFin());
                            getCurrentDateTime = c.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        estado = p.getEstadoProducto();

                        if (getCurrentDateTime.compareTo(fechaInicio) > 0 && getCurrentDateTime.compareTo(fechaFin) < 0 && !estado.equals("Cancelado por el vendedor")){

                           // Toast.makeText(salesOffered.this, getCurrentDateTime + " - " + fecha + " - " + getCurrentDateTime.compareTo(fecha) , Toast.LENGTH_SHORT).show();
                            titulo_ofertados.setText("Productos ofertados");
                            subtitulo_ofertados.setText("Los siguientes productos estan disponibles para la venta hasta que se cumpla su fecha y hora de fin");
                            listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                    p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                    p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(),1,p.getPrecioDomicilio(),
                                    p.getIdVendedor()));
                        }
                    }
                    listSaleAdapter = new ListSaleAdapter(salesOffered.this, listaDeDatos, salesOffered.this);
                    listado.setAdapter(listSaleAdapter);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(salesOffered.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}