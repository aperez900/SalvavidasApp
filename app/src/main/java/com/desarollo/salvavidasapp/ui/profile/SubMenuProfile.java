package com.desarollo.salvavidasapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.seller.seller2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SubMenuProfile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRefVendedores;

    public SubMenuProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRefVendedores = database.getReference("vendedores");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_menu_profile, container, false);

        CardView tvPerfilComprador = view.findViewById(R.id.tv_comprador);
        CardView tvPerfilVendedor = view.findViewById(R.id.tv_vendedor);
        ImageView imgComprador = view.findViewById(R.id.img_comprador);
        ImageView imgVendedor = view.findViewById(R.id.img_vendedor);

        consultarDatosComprador(imgComprador);
        consultarDatosVendedor(imgVendedor);

        tvPerfilComprador.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_subMenuProfile_to_nav_profile));

        tvPerfilVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), seller2.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //tvPerfilVendedor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_subMenuProfile_to_seller3));

        return view;
    }

    public void consultarDatosComprador(ImageView imgComprador){
        //consultando datos del usuario
        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            imgComprador.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        }else{
                            imgComprador.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.gray_light));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del usuario. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void consultarDatosVendedor(ImageView imgVendedor){
        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            imgVendedor.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red));
                        }else{
                            imgVendedor.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.gray_light));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error consultando los datos del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}