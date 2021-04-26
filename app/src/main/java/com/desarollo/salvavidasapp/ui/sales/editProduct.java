package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class editProduct extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String idProducto, nombreProducto, descripcionProducto, categoriaProducto, precioProducto, descuentoProducto,
            domicilioProducto, fechaInicio, fechaFin, horaInicio, horaFin;
    EditText et_nombreProducto, et_descripcion_producto, et_precio, et_descuento;
    TextView tv_fecha_inicio, tv_fecha_fin, tv_hora_inicio, tv_hora_fin;
    Spinner sp_categoria_producto, sp_domicilio;
    ImageView img_foto;
    Button btn_cancelar_producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        et_nombreProducto = findViewById(R.id.et_nombre_producto);
        et_descripcion_producto = findViewById(R.id.et_descripcion_producto);
        et_precio = findViewById(R.id.et_precio);
        et_descuento = findViewById(R.id.et_descuento);
        sp_categoria_producto = findViewById(R.id.sp_categoria_producto);
        sp_domicilio = findViewById(R.id.sp_domicilio);
        tv_fecha_inicio = findViewById(R.id.tv_fecha_inicio);
        tv_hora_inicio = findViewById(R.id.tv_hora_inicio);
        tv_fecha_fin = findViewById(R.id.tv_fecha_fin);
        tv_hora_fin = findViewById(R.id.tv_hora_fin);
        img_foto = findViewById(R.id.img_foto);
        btn_cancelar_producto = findViewById(R.id.btn_cancelar_producto);

        Intent intent = getIntent();
        if (intent != null){
            Bundle extras = getIntent().getExtras();
            idProducto = extras.getString("idProducto");
            nombreProducto = extras.getString("nombreProducto");
            descripcionProducto = extras.getString("descripcionProducto");
            categoriaProducto = extras.getString("categoriaProducto");
            precioProducto = extras.getString("precioProducto");
            descuentoProducto = extras.getString("descuentoProducto");
            domicilioProducto = extras.getString("domicilioProducto");
            fechaInicio = extras.getString("fechaInicio");
            fechaFin = extras.getString("fechaFin");
            horaInicio = extras.getString("horaInicio");
            horaFin = extras.getString("horaFin");
        }

        String[] ArrayCategorias = new String[]{categoriaProducto,"✚ Selecciona una categoría", "Comida preparada","Comida cruda"};
        ArrayList<String> listCategoriaProductos = new ArrayList(Arrays.asList(ArrayCategorias));
        ArrayAdapter<String> adapterCategoriaProductos = new ArrayAdapter<>(this, R.layout.spinner_item_modified, listCategoriaProductos);

        String[] ArrayDomicilio = new String[]{domicilioProducto,"✚ ¿Deseas ofrecer domicilio?", "Sí","No"};
        ArrayList<String> listDomicilioProductos = new ArrayList(Arrays.asList(ArrayDomicilio));
        ArrayAdapter<String> adapterDomicilioProductos = new ArrayAdapter<>(this, R.layout.spinner_item_modified, listDomicilioProductos);

        et_nombreProducto.setText(nombreProducto);
        et_descripcion_producto.setText(descripcionProducto);
        sp_categoria_producto.setAdapter(adapterCategoriaProductos);
        et_precio.setText(precioProducto);
        et_descuento.setText(descuentoProducto);
        sp_domicilio.setAdapter(adapterDomicilioProductos);
        tv_fecha_inicio.setText(fechaInicio);
        tv_hora_inicio.setText(horaInicio);
        tv_fecha_fin.setText(fechaFin);
        tv_hora_fin.setText(horaFin);

        cargarImagen(img_foto);

        btn_cancelar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearAlertDialog();
            }
        });

    }

    private void cargarImagen(ImageView img_foto) {

        myRef.child(currentUser.getUid()).child(idProducto).child("foto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String urlFoto = snapshot.getValue().toString();
                    Glide.with(editProduct.this)
                            .load(urlFoto)
                            .into(img_foto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void crearAlertDialog(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(editProduct.this);
        confirmacion.setMessage("¿Esta seguro que desea cancelar el producto?. Éste dejará de estar disponible para su compra.")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(editProduct.this,"Clic en Sí ",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog titulo = confirmacion.create();
        titulo.setTitle("Cancelar producto");
        titulo.show();
    }
}