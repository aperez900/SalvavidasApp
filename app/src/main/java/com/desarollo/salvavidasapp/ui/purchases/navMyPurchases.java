package com.desarollo.salvavidasapp.ui.purchases;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.seller.listRequestedProductsAdapter;

import java.util.ArrayList;

public class navMyPurchases extends Fragment {

    listRequestedProductsAdapter ListRequestedProductsAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();

    public navMyPurchases() {
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
                Intent intent = new Intent(navMyPurchases.this.getContext(), purchasesInProcess.class);
                startActivity(intent);
            }
        });


        cardCancelledPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(navMyPurchases.this.getContext(), cancelledPurchases.class);
                startActivity(intent);
            }
        });

        cardAnulledPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(navMyPurchases.this.getContext(), anulledPurchases.class);
                startActivity(intent);
            }
        });

        cardPurchasesMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(navMyPurchases.this.getContext(), purchasesMade.class);
                startActivity(intent);
            }
        });

        return view;
    }
}