package com.desarollo.salvavidasapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.TipoComidas;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class products_by_type extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    //ListTypeFood listTypeFood;
    //RecyclerView listado_tipo_comidas;
    //ArrayList<TipoComidas> listaDeDatosTipo = new ArrayList<>();
    //DatabaseReference myRefTypeFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_by_type);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");
        //myRefTypeFood = database.getReference("tipo_comidas");

        listado = findViewById(R.id.listado);
        //listado_tipo_comidas = findViewById(R.id.tipo_comidas);

        //listado_tipo_comidas.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        //listTypeFood = new ListTypeFood(getApplicationContext(),listaDeDatosTipo,products_by_type.this);
        //listado_tipo_comidas.setAdapter(listTypeFood);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, products_by_type.this);

        listado.setAdapter(listSellAdapter);

        crearListado();
    }


/*
    private void crearListadoSubTipo() {
        myRefTypeFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatosTipo.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        TipoComidas t = objsnapshot.getValue(TipoComidas.class);

                        listaDeDatosTipo.add(new TipoComidas(t.getTipoComida(),t.getFoto()));
                        // Toast.makeText(getApplicationContext(), t.getTipoComida(), Toast.LENGTH_SHORT).show();
                    }

                    listTypeFood = new ListTypeFood(getApplicationContext(),listaDeDatosTipo, products_by_type.this);
                    listado_tipo_comidas.setAdapter(listTypeFood);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    private void crearListado() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //Recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);

                            listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                    p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                    p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa()));
                            //Toast.makeText(getApplicationContext(), p.getfoto(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    listSellAdapter = new ListSellAdapter(getApplicationContext(), listaDeDatos, products_by_type.this);
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