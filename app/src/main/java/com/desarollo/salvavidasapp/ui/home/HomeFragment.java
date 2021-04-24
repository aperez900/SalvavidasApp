package com.desarollo.salvavidasapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
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
    RecyclerView listado;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = view.findViewById(R.id.listado);
        listado.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));

        ListSellAdapter adaptador = new ListSellAdapter(getApplicationContext(),listaDeDatos);
        listado.setAdapter(adaptador);
        crearListado();
        return view;
    }

    private void crearListado() {

        listaDeDatos.add(new Productos(
                "Hamburguesas",
                "Otra cosa",
                "Esto es una comida realizada el dia de ayer",
                "Comidas preparadas",
                1.0,1.0,"","",1,"","","",""));

        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        String idProducto = objsnapshot.child("idProducto").getValue().toString();
                        String nombreProducto = objsnapshot.child("nombreProducto").getValue().toString();
                        String descripcionProducto = objsnapshot.child("descripcionProducto").getValue().toString();
                        String categoriaProducto = objsnapshot.child("categoriaProducto").getValue().toString();

                       // Toast.makeText(getApplicationContext(),idProducto, Toast.LENGTH_SHORT).show();
                        listaDeDatos.add(new Productos(idProducto,nombreProducto,descripcionProducto,categoriaProducto,1.0,1.0,"","",1,"","","",""));

                    }

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