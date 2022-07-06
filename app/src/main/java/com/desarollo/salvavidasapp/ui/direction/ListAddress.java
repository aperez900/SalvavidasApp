package com.desarollo.salvavidasapp.ui.direction;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.sales.addPhoto;
import com.desarollo.salvavidasapp.ui.sales.addProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

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
        recyclerViewDirecciones = (RecyclerView) view.findViewById(R.id.recycle_address);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerViewDirecciones.setLayoutManager(manager);
        //recyclerViewDirecciones.setHasFixedSize(true);
        listAddressAdapter = new ListAddressAdapter(getApplicationContext(),listaDirecciones,getActivity());
        recyclerViewDirecciones.setAdapter(listAddressAdapter);

        Button btnAgregarDir = view.findViewById(R.id.btnAgregarDirecciones);
        Button btnUbicacionActual = view.findViewById(R.id.btnUbicacionActual);
        listaDirecciones = new ArrayList<>();
        //cargar la lista
        cargarLista();

        btnUbicacionActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gpsActivo = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (gpsActivo != false){
                        Intent h = new Intent(getContext(), Maps.class);
                        startActivity(h);
                }
                else{
                    Toast.makeText(getContext(),"activa el GPS para poder continuar...",Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        myRef.child(currentUser.getUid()).child("mis_direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDirecciones.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        ListDirecciones d = objsnapshot.getValue(ListDirecciones.class);
                        listaDirecciones.add(new ListDirecciones(d.getNombreDireccion(),d.getDireccionUsuario(), d.getMunicipioDireccion(),R.drawable.marker,d.getSeleccion(), d.getLat(), d.getLng()));
                    }

                    listAddressAdapter = new ListAddressAdapter(getApplicationContext(),listaDirecciones,getActivity());
                    recyclerViewDirecciones.setAdapter(listAddressAdapter);

                    //Acciones al dar clic en un item de la lista
                    listAddressAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String nombre_seleccion = listaDirecciones.get(recyclerViewDirecciones.getChildAdapterPosition(view)).getNombreDireccion();
                            direccionPrincipal(nombre_seleccion);
                        }
                    });
                }else{
                    //mostrar los datos por defecto
                    mostrarData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando las direcciones", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void mostrarData(){
        listaDirecciones.add(new ListDirecciones("Nombre dirección 1", "Dirección 1","Ciudad/Departamento",R.drawable.marker,"false",0.0,0.0));
        listaDirecciones.add(new ListDirecciones("Nombre dirección 2", "Dirección 2","Ciudad/Departamento",R.drawable.marker,"false",0.0,0.0));

        recyclerViewDirecciones.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listAddressAdapter = new ListAddressAdapter(getApplicationContext(),listaDirecciones,getActivity());
        recyclerViewDirecciones.setAdapter(listAddressAdapter);
    }

    public void agregarDirecciones(View v) {
        Intent h = new Intent(getApplicationContext(), FrmAddress.class);
        startActivity(h);
    }


    String check = "";
    Map<String,String> selectedItems = new HashMap<>();

    public void direccionPrincipal(String nombre_seleccion){


        myRef.child(currentUser.getUid()).child("mis_direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){

                        if (objsnapshot.child("nombreDireccion").exists()) {
                            String nombre = Objects.requireNonNull(objsnapshot.child("nombreDireccion").getValue()).toString();

                            if(nombre_seleccion.equals(nombre)){
                                check= "true";

                            }
                            else{
                                check = "false";
                            }
                            selectedItems.put(nombre,check);
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando las direcciones", Toast.LENGTH_SHORT).show();
            }


        });

        for (Map.Entry<String, String> entry : selectedItems.entrySet()) {


            myRef.child(currentUser.getUid()).child("mis direcciones").child(entry.getKey()).child("seleccion").setValue(entry.getValue())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            //Toast.makeText(getApplicationContext(), entry.getKey() + " " + entry.getValue() , Toast.LENGTH_SHORT).show();

        }

        selectedItems.clear();
    }
}