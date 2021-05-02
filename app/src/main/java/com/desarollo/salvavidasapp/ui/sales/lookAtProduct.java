package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.Date;

public class lookAtProduct extends AppCompatActivity {
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String idProducto ="";
    String urlFoto="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_at_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        ImageView imgProducto = findViewById(R.id.img_imagen_producto);
        TextView nombreProducto = findViewById(R.id.tv_nombre_producto);
        TextView descripcionProducto = findViewById(R.id.tv_descripcion_producto);
        TextView precioProducto = findViewById(R.id.tv_precio_producto);
        TextView descuentoProducto = findViewById(R.id.tv_descuento_producto);
        TextView tvporcDescuento = findViewById(R.id.tv_porc_descuento);
        TextView totalProducto = findViewById(R.id.tv_total_producto);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProducto = extras.getString("idProducto");
            nombreProducto.setText(extras.getString("nombreProducto"));
            descripcionProducto.setText(extras.getString("descripcionProducto"));
            Double precio = Double.parseDouble(extras.getString("precio"));
            precioProducto.setText(String.valueOf(precio));
            precioProducto.setPaintFlags(precioProducto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Double descuento = Double.parseDouble(extras.getString("descuento"));
            descuentoProducto.setText(String.valueOf(descuento));
            long porcDescuento = Math.round(descuento/precio*100);
            tvporcDescuento.setText(String.valueOf(-porcDescuento));
            Double total = precio-descuento;
            totalProducto.setText(String.valueOf(total));
            //ScategoriaProductos = extras.getString("tipoProducto");
            //SdomicilioProducto = extras.getString("domicilioProducto");
            //tvFechaInicio.setText(extras.getString("fechaInicio"));
            //tvFechaFin.setText(extras.getString("fechaFin"));
            //tvHoraInicio.setText(extras.getString("horaInicio"));
            //tvHoraFin.setText(extras.getString("horaFin"));
            //type = extras.getString("tipyEntry");
            //fotoConsulta = extras.getString("getUrlFoto");
        }

        consultarImagen(imgProducto);

    }

    public void consultarImagen(ImageView imgProducto){

        myRef.child(currentUser.getUid()).child(idProducto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    urlFoto = snapshot.child("foto").getValue().toString();
                    Glide.with(lookAtProduct.this)
                            .load(urlFoto)
                            .into(imgProducto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}