package com.desarollo.salvavidasapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.SubTipoProductos;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class
productsByType extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    DatabaseReference myRef;

    ListSubTypeFood listSubTypeFood;
    RecyclerView listadoSubtipoProductos;
    ArrayList<SubTipoProductos> listaDeDatosSubTipo = new ArrayList<>();
    DatabaseReference myRefTypeFood;
    String tipoProducto;
    TextView tituloSubTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_by_type);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");
        myRefTypeFood = database.getReference("tipo_comidas");
        tituloSubTipo = findViewById(R.id.tv_titulo_subtipo);

        listadoSubtipoProductos = findViewById(R.id.sub_tipo_comidas);
        listadoSubtipoProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        listSubTypeFood = new ListSubTypeFood(getApplicationContext(),listaDeDatosSubTipo, productsByType.this);
        listadoSubtipoProductos.setAdapter(listSubTypeFood);

        listado = findViewById(R.id.listado);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        listado.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, productsByType.this);

        listado.setAdapter(listSellAdapter);

        tipoProducto = "";

        Intent intent = getIntent();
        if (intent.getExtras()  != null){
            Bundle extras = getIntent().getExtras();
            tipoProducto = extras.getString("TipoComida");

        }

        crearListadoSubTipo();
        crearListado();
    }


    private void crearListadoSubTipo() {
        myRefTypeFood.child(tipoProducto).child("subTipo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatosSubTipo.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        SubTipoProductos st = objsnapshot.getValue(SubTipoProductos.class);

                        listaDeDatosSubTipo.add(new SubTipoProductos(st.getSubTipoComida(),st.getFoto(), tipoProducto));

                    }

                    listSubTypeFood = new ListSubTypeFood(getApplicationContext(),listaDeDatosSubTipo, productsByType.this);
                    listadoSubtipoProductos.setAdapter(listSubTypeFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void crearListado() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //Recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);

                            if (p.getCategoriaProducto().equals(tipoProducto)){
                                String estado="";
                                String subCategoria="";
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
                                subCategoria = p.getSubCategoriaProducto();

                                if (getCurrentDateTime.compareTo(fechaInicio) > 0 && getCurrentDateTime.compareTo(fechaFin) < 0
                                        && !estado.equals("Cancelado por el vendedor")){

                                    tituloSubTipo.setVisibility(View.INVISIBLE);
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(), 1, p.getCantidadDisponible(),p.getPrecioDomicilio(),
                                            p.getIdVendedor()));

                                }
                            }
                        }
                    }

                    listSellAdapter = new ListSellAdapter(getApplicationContext(), listaDeDatos, productsByType.this);
                    listado.setAdapter(listSellAdapter);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

}