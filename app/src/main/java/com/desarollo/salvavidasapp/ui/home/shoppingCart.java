package com.desarollo.salvavidasapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class shoppingCart extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    listShoppingCartAdapter ListShoppingCartAdapter;
    TextView titulo_carrito, subtitulo_carrito, total_carrito;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefProductos;
    Double totalCarrito=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        titulo_carrito = findViewById(R.id.tv_titulo_carrito);
        subtitulo_carrito = findViewById(R.id.tv_subtitulo_carrito);
        total_carrito = findViewById(R.id.tv_total_carrito);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRefProductos = database.getReference("productos");

        listado = findViewById(R.id.listadoCarrito);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        ListShoppingCartAdapter = new listShoppingCartAdapter(this, listaDeDatos,this);
        listado.setAdapter(ListShoppingCartAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    subtitulo_carrito.setText("Los siguientes productos estan disponibles en el carrito hasta que se cumpla su fecha y hora de fin");
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        String idProd = objsnapshot.child("idProducto").getValue().toString();
                        String cantidad = objsnapshot.child("cantidadProducto").getValue().toString();

                        consultarDetalleProducto(idProd,cantidad);

                    }
                }else{
                    Intent intent = new Intent(shoppingCart.this , Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(shoppingCart.this, "Carrito de compras vacío", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(shoppingCart.this, "Error cargando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String cantidad){
        totalCarrito=0.0;
        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            String id = p.getIdProducto();
                            if(idProduct.equals(id)) {
                                listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad)));
                                totalCarrito = totalCarrito + (p.getPrecio()-p.getDescuento())*Integer.parseInt(cantidad);
                            }
                        }
                    }
                }
                ListShoppingCartAdapter = new listShoppingCartAdapter(shoppingCart.this, listaDeDatos, shoppingCart.this);
                listado.setAdapter(ListShoppingCartAdapter);
                String patron = "###,###.##";
                DecimalFormat objDF = new DecimalFormat (patron);
                total_carrito.setText("Total: $" + objDF.format(totalCarrito));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}