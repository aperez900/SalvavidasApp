package com.desarollo.salvavidasapp.ui.direction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    Controlador del activity que muestra la lista de direcciones
 */
public class ListAddress extends Fragment {

    ListAddressAdapter listAddressAdapter;
    RecyclerView recyclerViewDirecciones;
    ArrayList<ListDirecciones> listaDirecciones;
    TextView btnAgregarDirecciones;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_address, container, false);
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        recyclerViewDirecciones = view.findViewById(R.id.recycle_address);
        btnAgregarDirecciones = view.findViewById(R.id.btnAgregarDirecciones);
        Button btnAgregarDir = view.findViewById(R.id.btnAgregarDirecciones);
        listaDirecciones = new ArrayList<>();
        //cargar la lista
        cargarLista();


        btnAgregarDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getContext(), FrmAddress.class);
                startActivity(h);
            }
        });
        return view;
    }

    public void cargarLista(){
        myRef.child(currentUser.getUid()).child("mis direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDirecciones.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        ListDirecciones d = objsnapshot.getValue(ListDirecciones.class);
                        listaDirecciones.add(new ListDirecciones(d.getNombreDireccion(),d.getDireccionUsuario(), d.getMunicipioDireccion(),R.drawable.ic_icono_address));
                    }
                    recyclerViewDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
                    listAddressAdapter = new ListAddressAdapter(getContext(),listaDirecciones);
                    recyclerViewDirecciones.setAdapter(listAddressAdapter);
                }else{
                    //mostrar los datos por defecto
                    mostrarData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
    public void mostrarData(){
        listaDirecciones.add(new ListDirecciones("Nombre dirección 1", "Dirección 1","Ciudad/Departamento",R.drawable.ic_icono_address));
        listaDirecciones.add(new ListDirecciones("Nombre dirección 2", "Dirección 2","Ciudad/Departamento",R.drawable.ic_icono_address));

        recyclerViewDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        listAddressAdapter = new ListAddressAdapter(getContext(),listaDirecciones);
        recyclerViewDirecciones.setAdapter(listAddressAdapter);

        //Acciones al dar clic en un item de la lista
        listAddressAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreDireccion = listaDirecciones.get(recyclerViewDirecciones.getChildAdapterPosition(view)).getNombreDireccion();
                Toast.makeText(getContext(),"Seleccionó: " + nombreDireccion,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Acceso por firebase con email and clave
    public void agregarDirecciones(View v) {
        Intent h = new Intent(getContext(), FrmAddress.class);
        startActivity(h);
    }
}