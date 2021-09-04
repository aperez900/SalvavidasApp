package com.desarollo.salvavidasapp.ui.sales;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Vendedores;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sales extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores;
    Vendedores v;

    public sales() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");

    } //fin OnCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sales, container, false);

        CardView cardAddProduct = view.findViewById(R.id.cardAddProduct);
        CardView cardScheduledSales = view.findViewById(R.id.cardScheduledSales);
        CardView cardSalesOffered = view.findViewById(R.id.cardSalesOffered);
        CardView cardAnulledSales = view.findViewById(R.id.cardAnulledSales);
        CardView cardCancelledSales = view.findViewById(R.id.cardCancelledSales);
        CardView cardUnsoldOffers = view.findViewById(R.id.cardUnsoldOffers);

        consultarDatosVendedor();

        //cardAddProduct.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_sales_to_addProduct));
        cardAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), addProduct.class);
                startActivity(intent);
            }
        });

        //cardScheduledSales.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_sales_to_scheduleSales));

        cardScheduledSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), scheduledSales.class);
                startActivity(intent);
            }
        });

        //cardSalesOffered.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_sales_to_salesOffered));

        cardSalesOffered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), salesOffered.class);
                startActivity(intent);
            }
        });

        //cardCancelledSales.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_sales_to_cancelledSales));

        cardCancelledSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), cancelledSales.class);
                startActivity(intent);
            }
        });

        cardUnsoldOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), unsoldOffers.class);
                startActivity(intent);
            }
        });

        cardAnulledSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales.this.getContext(), unsoldOffers.class);
                startActivity(intent);
            }
        });

        return view;
    } // fin OnCreateView

    public void consultarDatosVendedor(){
        //consultando datos del vendedor
        myRefVendedores.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String estado = snapshot.child("estado").getValue().toString();
                            if (estado.equals("Pendiente")){
                                Toast.makeText(getContext(), "Su solicitud de vendedor aún se encuentra pendiente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), Home.class);
                                startActivity(intent);
                            }else{
                                //Toast.makeText(getContext(), "Autorizado", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Para poder usar esta opción, debes diligenciar el perfil de vendedor y ser aprobado",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), Home.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error consultando el estado del vendedor. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}