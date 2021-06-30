package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.seller.requested_products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class buyProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores;
    String idProducto ="";
    String idVendedor ="";
    String nombreProducto;
    Double precioProducto, precioDomicilio;
    int nroProductos;
    TextView tvPrecioProducto, tvPrecioDomicilio, tvValorComision, tvTotal, tvNombreProducto,
                tvCantidadProducto, tvEstadoProducto, tvSubTotalProducto, tvSubTotalProducto1,
                tvSignoMonedaSubTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");

        tvPrecioProducto = findViewById(R.id.tv_precio_producto);
        tvPrecioDomicilio = findViewById(R.id.tv_precio_domicilio);
        tvValorComision = findViewById(R.id.tv_valor_comision);
        tvTotal = findViewById(R.id.tv_total_producto);
        tvNombreProducto = findViewById(R.id.tv_nombre_producto);
        tvCantidadProducto = findViewById(R.id.tv_cantidad_producto);
        tvEstadoProducto = findViewById(R.id.tv_estado_Producto);
        tvSubTotalProducto = findViewById(R.id.tv_subTotal);
        tvSubTotalProducto1 = findViewById(R.id.tv_subTotal1);
        tvSignoMonedaSubTotal = findViewById(R.id.tv_signo_moneda1);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            precioProducto = Double.parseDouble(extras.getString("totalProducto"));
            precioDomicilio = Double.parseDouble(extras.getString("precioDomicilio"));
            nroProductos = Integer.parseInt(extras.getString("nroProductos"));
            idVendedor = extras.getString("idVendedor");

            Double valorComision = precioProducto * nroProductos * 0.06;

            tvPrecioProducto.setText(String.valueOf(precioProducto*nroProductos));
            tvPrecioDomicilio.setText(String.valueOf(precioDomicilio));
            tvValorComision.setText(String.valueOf(valorComision));
            tvTotal.setText(String.valueOf(precioProducto*nroProductos + precioDomicilio + valorComision));
            tvNombreProducto.setText(nombreProducto);
            tvCantidadProducto.setText(String.valueOf(nroProductos));
            tvSubTotalProducto.setText(String.valueOf(precioProducto*nroProductos));
            tvSubTotalProducto.setPaintFlags(tvSubTotalProducto.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvSubTotalProducto1.setPaintFlags(tvSubTotalProducto1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvSignoMonedaSubTotal.setPaintFlags(tvSignoMonedaSubTotal.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        consultarEstadoProductoEnTramite(idProducto);
    }

    public void consultarEstadoProductoEnTramite(String idProducto) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRefVendedores.child(idVendedor).child("productos_en_tramite").child(currentUser.getUid()).child(idProducto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String estadoSolicitud = snapshot.child("estado").getValue().toString();
                    tvEstadoProducto.setText(estadoSolicitud);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyProduct.this, "Error cargando el estado del Producto", Toast.LENGTH_SHORT).show();
            }
        });
    }

}