package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class scheduledSales2 extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_sales2);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoProgramados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(this,listaDeDatos, this);
        listado.setAdapter(listSellAdapter);

        crearListado();
    }

    private void crearListado() {

        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy");

        myRef
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
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
                                if (fecha.after(hoy) &&  !estado.equals("Cancelado")){

                                    // Toast.makeText(getContext(), p.getFechaInicio(), Toast.LENGTH_SHORT).show();
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin()));
                                }
                            }
                            listSellAdapter = new ListSellAdapter(scheduledSales2.this,listaDeDatos, scheduledSales2.this);
                            listado.setAdapter(listSellAdapter);
                            //Acciones al dar clic en un item de la lista
                            listSellAdapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String nombreProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getNombreProducto();
                                    Toast.makeText(getApplicationContext(),"Seleccionó: " + nombreProducto,Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}