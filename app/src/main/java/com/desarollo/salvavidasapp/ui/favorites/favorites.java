package com.desarollo.salvavidasapp.ui.favorites;

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
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Favoritos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class favorites extends Fragment {

    ListFavoritesAdapter listFavoritesAdapter;
    RecyclerView recyclerViewFavorites;
    ArrayList<Favoritos> listaSubTipo;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    DatabaseReference myRef;
    DatabaseReference myRefTipoC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefTipoC = database.getReference("tipo_comidas");
        myRef = database.getReference("usuarios");

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerViewFavorites = (RecyclerView) view.findViewById(R.id.recycle_favoritos);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFavorites.setLayoutManager(manager);
//        recyclerViewFavorites.setHasFixedSize(true);
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
        consultarComidas();

        return view;
    }



    public void cargarLista() {
        myRefTipoC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaSubTipo.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) {

                        for(DataSnapshot objsnapshot2 : objsnapshot.child("subTipo").getChildren()) {
                            Favoritos d = objsnapshot2.getValue(Favoritos.class);
                            listaSubTipo.add(new Favoritos(d.getSubTipoComida(), d.getFoto(), false));

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando las direcciones", Toast.LENGTH_SHORT).show();
            }
        });

    }

    Boolean estado;
    public void consultarComidas() {

        myRef.child(currentUser.getUid()).child("comidas_preferidas")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot objsnapshot : snapshot.getChildren()) {
                                for(int i = 0; i <= listaSubTipo.size(); i++){
                                    String tipo = objsnapshot.getKey().toString();
                                    String estado_ = objsnapshot.getValue().toString();
                                    estado = Boolean.parseBoolean(estado_);
                                    Favoritos fav = new Favoritos(tipo,listaSubTipo.get(i).getFoto(),estado);

                                    if (tipo.equals(listaSubTipo.get(i).getSubTipoComida())){
                                        listaSubTipo.set(i,fav);
                                        break;
                                    }
                                }
                            }

                        }

                        listFavoritesAdapter = new ListFavoritesAdapter(getApplicationContext(), listaSubTipo, getActivity());
                        recyclerViewFavorites.setAdapter(listFavoritesAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }
}
