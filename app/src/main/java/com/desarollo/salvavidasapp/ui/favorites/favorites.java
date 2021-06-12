package com.desarollo.salvavidasapp.ui.favorites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
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

import com.desarollo.salvavidasapp.Models.Favoritos;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.SubTipoComidas;
import com.desarollo.salvavidasapp.Models.TipoComidas;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.FrmAddress;
import com.desarollo.salvavidasapp.ui.direction.ListAddressAdapter;
import com.desarollo.salvavidasapp.ui.direction.Maps;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.home.HomeFragment;
import com.desarollo.salvavidasapp.ui.home.HomeViewModel;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.desarollo.salvavidasapp.ui.home.ListTypeFood;
import com.desarollo.salvavidasapp.ui.home.listShoppingCartAdapter;
import com.desarollo.salvavidasapp.ui.sales.addPhoto;
import com.desarollo.salvavidasapp.ui.sales.addProduct;
import com.desarollo.salvavidasapp.ui.seller.seller2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;


public class favorites extends Fragment {

    ListFavoritesAdapter listFavoritesAdapter;
    RecyclerView recyclerViewFavorites;
    ArrayList<Favoritos> listaSubTipo;

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
        myRef = database.getReference("tipo_comidas");

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerViewFavorites = (RecyclerView) view.findViewById(R.id.recycle_favoritos);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFavorites.setLayoutManager(manager);
        recyclerViewFavorites.setHasFixedSize(true);
        listFavoritesAdapter = new ListFavoritesAdapter(getApplicationContext(), listaSubTipo, getActivity());
        recyclerViewFavorites.setAdapter(listFavoritesAdapter);

        Button btnAgregar = view.findViewById(R.id.btnAgregarFavoritos);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Home.class);
                startActivity(intent);

            }
        });


        listaSubTipo = new ArrayList<>();

        //cargar la lista
        cargarLista();

        return view;
    }



    public void cargarLista() {

        myRef.child("comida cruda").child("subTipo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaSubTipo.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) {
                        Favoritos d = objsnapshot.getValue(Favoritos.class);

                        listaSubTipo.add(new Favoritos(d.getSubTipoComida(), d.getFoto(), false));

                    }

                    listFavoritesAdapter = new ListFavoritesAdapter(getApplicationContext(), listaSubTipo, getActivity());
                    recyclerViewFavorites.setAdapter(listFavoritesAdapter);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando las direcciones", Toast.LENGTH_SHORT).show();
            }
        });

    }
    Boolean estado;
    public Boolean consultar(String tipo) {


        myRef.child(currentUser.getUid()).child("comidas_preferidas").child(tipo)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists()) {
                            String estado_ = snapshot2.getValue().toString();
                            estado = Boolean.parseBoolean(estado_);
                        } else {
                            estado = false;
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }



                });

        if (estado.equals(null)){
            estado = false;
        }
        return estado;

    }
}
