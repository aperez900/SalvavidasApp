package com.desarollo.salvavidasapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.TipoComidas;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.ListAddressAdapter;
import com.desarollo.salvavidasapp.ui.sales.addProduct;
import com.desarollo.salvavidasapp.ui.sales.sales;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    ArrayList<TipoComidas> listaDeDatosTipo = new ArrayList<>();
    RecyclerView listado_comidas,listado_tipo_comidas;
    ListSellAdapter listSellAdapter;
    ListTypeFood listTypeFood;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefTypeFood;
    DatabaseReference myRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefTypeFood = database.getReference("tipo_comidas");
        myRef = database.getReference("productos");


        listado_comidas = view.findViewById(R.id.listado);
        listado_tipo_comidas = view.findViewById(R.id.tipo_comidas);

        listado_tipo_comidas.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo,getActivity());
        listado_tipo_comidas.setAdapter(listTypeFood);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listado_comidas.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getContext(),listaDeDatos, getActivity());

        listado_comidas.setAdapter(listSellAdapter);

        crearListadoTipo();
        crearListado();



        return view;
    }


    private void crearListadoTipo() {
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

                    listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo, getActivity());
                    listado_tipo_comidas.setAdapter(listTypeFood);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void crearListado() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    String estado="";
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            estado = p.getEstadoProducto();

                            if (!estado.equals("Cancelado")) {
                                listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin()));
                                //Toast.makeText(getApplicationContext(), p.getfoto(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    listSellAdapter = new ListSellAdapter(getContext(),listaDeDatos, getActivity());
                    listado_comidas.setAdapter(listSellAdapter);
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