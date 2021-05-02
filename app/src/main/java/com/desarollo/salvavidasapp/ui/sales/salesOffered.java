package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class salesOffered extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSaleAdapter listSaleAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_offered);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoOfertados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        listado.setHasFixedSize(true);
        listSaleAdapter = new ListSaleAdapter(this, listaDeDatos,this);
        listado.setAdapter(listSaleAdapter);

        crearListado();
    }

    private void crearListado() {

        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy");

        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        Productos p = objsnapshot.getValue(Productos.class);
                        Date fecha = null;
                        Date hoy = new Date();
                        String estado="";
                        try {
                            fecha = fechaHora.parse(p.getFechaInicio());
                            estado = p.getEstadoProducto();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (fecha.before(hoy) &&  !estado.equals("Cancelado")){

                            //Toast.makeText(getContext(), p.getFechaInicio(), Toast.LENGTH_SHORT).show();
                            listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                    p.getCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                    p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin()));

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