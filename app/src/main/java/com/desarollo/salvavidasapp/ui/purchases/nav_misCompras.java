package com.desarollo.salvavidasapp.ui.purchases;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.addProduct;
import com.desarollo.salvavidasapp.ui.sales.sales;
import com.desarollo.salvavidasapp.ui.seller.listRequestedProductsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class nav_misCompras extends Fragment {

    listRequestedProductsAdapter ListRequestedProductsAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();

    public nav_misCompras() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_mis_compras, container, false);

        CardView cardPurchasesInProcess = view.findViewById(R.id.cardPurchasesInProcess);
        CardView cardCancelledPurchases = view.findViewById(R.id.cardCancelledPurchases);
        CardView cardPurchasesMade = view.findViewById(R.id.cardPurchasesMade);
        CardView cardAnulledPurchases = view.findViewById(R.id.cardAnulledPurchases);

        cardPurchasesInProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nav_misCompras.this.getContext(), purchasesInProcess.class);
                startActivity(intent);
            }
        });


        cardCancelledPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nav_misCompras.this.getContext(), cancelled_purchases.class);
                startActivity(intent);
            }
        });

        cardAnulledPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nav_misCompras.this.getContext(), anulled_purchases.class);
                startActivity(intent);
            }
        });

        cardPurchasesMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nav_misCompras.this.getContext(), purchases_made.class);
                startActivity(intent);
            }
        });

        return view;
    }
}