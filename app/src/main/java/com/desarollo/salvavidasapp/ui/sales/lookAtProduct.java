package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class lookAtProduct extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefCarrito;
    String idProducto ="";
    String urlFoto="";
    int numeroProductos = 1;
    Productos p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_at_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");
        myRefCarrito = database.getReference("usuarios");

        ImageView imgProducto = findViewById(R.id.img_imagen_producto);
        TextView tvnombreProducto = findViewById(R.id.tv_nombre_producto);
        TextView tvdescripcionProducto = findViewById(R.id.tv_descripcion_producto);
        TextView tvprecioProducto = findViewById(R.id.tv_precio_producto);
        TextView tvdescuentoProducto = findViewById(R.id.tv_descuento_producto);
        TextView tvporcDescuento = findViewById(R.id.tv_porc_descuento);
        TextView tvtotalProducto = findViewById(R.id.tv_total_producto);
        TextView tvinicioProducto = findViewById(R.id.tv_fecha_inicio_producto);
        TextView tvfinProducto = findViewById(R.id.tv_fecha_fin_producto);
        TextView tvañadirCarrito = findViewById(R.id.tv_añadir_carrito);
        Button btnMas = findViewById(R.id.btn_mas);
        Button btnMenos = findViewById(R.id.btn_menos);
        TextView tvNumeroProductos = findViewById(R.id.tv_numero_productos);
        ImageView imgAñadirCarrito = findViewById(R.id.img_shop);
        tvañadirCarrito.setPaintFlags(tvprecioProducto.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            Bundle extras = getIntent().getExtras();

            idProducto = extras.getString("idProducto");
            tvnombreProducto.setText(extras.getString("nombreProducto"));
            tvdescripcionProducto.setText(extras.getString("descripcionProducto"));
            tvprecioProducto.setText(String.valueOf(extras.getString("precio")));
            tvprecioProducto.setPaintFlags(tvprecioProducto.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Double precio = Double.parseDouble(extras.getString("precio"));
            Double descuento = Double.parseDouble(extras.getString("descuento"));
            tvdescuentoProducto.setText(String.valueOf(descuento));
            long porcDescuento = Math.round(descuento/precio*100);
            tvporcDescuento.setText(String.valueOf(-porcDescuento));
            Double total = precio-descuento;
            tvtotalProducto.setText(String.valueOf(total));
            //ScategoriaProductos = extras.getString("tipoProducto");
            //SdomicilioProducto = extras.getString("domicilioProducto");
            tvinicioProducto.setText(extras.getString("fechaInicio") + " " + extras.getString("horaInicio"));
            tvfinProducto.setText(extras.getString("fechaFin") + " " + extras.getString("horaFin"));
            //tvHoraInicio.setText(extras.getString("horaInicio"));
            //tvHoraFin.setText(extras.getString("horaFin"));
            //type = extras.getString("tipyEntry");
            //fotoConsulta = extras.getString("getUrlFoto");
        }

        consultarImagen(imgProducto);

        tvañadirCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCarrito();
                //Toast.makeText(lookAtProduct.this, "En construcción", Toast.LENGTH_SHORT).show();
            }
        });

        imgAñadirCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCarrito();
                //Toast.makeText(lookAtProduct.this, "En construcción", Toast.LENGTH_SHORT).show();
            }
        });

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroProductos = numeroProductos+1;
                tvNumeroProductos.setText(String.valueOf(numeroProductos));
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroActual;
                numeroActual = tvNumeroProductos.getText().toString();
                if (numeroActual.equals("1")) {

                }else{
                    numeroProductos = numeroProductos-1;
                    tvNumeroProductos.setText(String.valueOf(numeroProductos));
                }
            }
        });
    }

    public void consultarImagen(ImageView imgProducto){

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            if (objsnapshot2.child("idProducto").getValue().toString().equals(idProducto)) {
                                urlFoto = objsnapshot2.child("foto").getValue().toString();
                                Glide.with(getApplicationContext())
                                        .load(urlFoto)
                                        .into(imgProducto);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(lookAtProduct.this, "Error cargando la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void agregarCarrito(){

        //guarda los datos del carrito de compras
        myRefCarrito.child(currentUser.getUid()).child("carrito_compras").child(idProducto).setValue(idProducto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(lookAtProduct.this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(lookAtProduct.this, "Error agregando producto al carrito. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}