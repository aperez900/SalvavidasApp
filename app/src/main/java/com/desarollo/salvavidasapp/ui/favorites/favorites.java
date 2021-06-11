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

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.SubTipoComidas;
import com.desarollo.salvavidasapp.Models.TipoComidas;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.FrmAddress;
import com.desarollo.salvavidasapp.ui.direction.ListAddressAdapter;
import com.desarollo.salvavidasapp.ui.direction.Maps;
import com.desarollo.salvavidasapp.ui.home.HomeViewModel;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.desarollo.salvavidasapp.ui.home.ListTypeFood;
import com.desarollo.salvavidasapp.ui.home.listShoppingCartAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    ArrayList<SubTipoComidas> listaSubTipo;

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
        View view = inflater.inflate(R.layout.fragment_favorites,container,false);

        recyclerViewFavorites = (RecyclerView) view.findViewById(R.id.recycle_favoritos);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFavorites.setLayoutManager(manager);
        recyclerViewFavorites.setHasFixedSize(true);
        listFavoritesAdapter = new ListFavoritesAdapter(getApplicationContext(),listaSubTipo,getActivity());
        recyclerViewFavorites.setAdapter(listFavoritesAdapter);

        Button btnAgregar = view.findViewById(R.id.btnAgregarFavoritos);


        listaSubTipo = new ArrayList<>();

        //cargar la lista
        cargarLista();

        return view;
    }

    public void cargarLista(){
        myRef.child("comida cruda").child("subTipo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaSubTipo.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        SubTipoComidas d = objsnapshot.getValue(SubTipoComidas.class);
                        listaSubTipo.add(new SubTipoComidas(d.getSubTipoComida(), d.getFoto()));

                    }

                    listFavoritesAdapter = new ListFavoritesAdapter(getApplicationContext(),listaSubTipo,getActivity());
                    recyclerViewFavorites.setAdapter(listFavoritesAdapter);

                    //Acciones al dar clic en un item de la lista
                    listFavoritesAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String nombre_seleccion = listaSubTipo.get(recyclerViewFavorites.getChildAdapterPosition(view)).getSubTipoComida();

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

    }

}