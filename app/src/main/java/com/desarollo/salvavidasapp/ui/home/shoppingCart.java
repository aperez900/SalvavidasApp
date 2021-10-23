package com.desarollo.salvavidasapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.buyProduct;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class shoppingCart extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    listShoppingCartAdapter ListShoppingCartAdapter;
    TextView tituloCarrito, subtituloCarrito, totalCarrito;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef, myRefProductos, myRefVendedor;
    Double subTotalCarrito=0.0;
    ProgressDialog cargando;
    HashMap<String, String> producto = new HashMap<String, String>();
    Session session;
    String nombreComprador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cargando = new ProgressDialog(this);

        tituloCarrito = findViewById(R.id.tv_titulo_carrito);
        subtituloCarrito = findViewById(R.id.tv_subtitulo_carrito);
        totalCarrito = findViewById(R.id.tv_subtotal_carrito);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        myRefProductos = database.getReference("productos");
        myRefVendedor = database.getReference("vendedores");

        listado = findViewById(R.id.listadoCarrito);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        ListShoppingCartAdapter = new listShoppingCartAdapter(this, listaDeDatos,this);
        listado.setAdapter(ListShoppingCartAdapter);

        crearListado();

        //Actualiza los datos del perfil logeado en el fragmenProfile
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                nombreComprador = profile.getDisplayName();
            }
        }
    }

    public void crearListado() {

        myRef.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    subtituloCarrito.setText("Los siguientes productos estan disponibles en el carrito hasta que se cumpla su fecha y hora de fin");
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        String idProd = objsnapshot.child("idProducto").getValue().toString();
                        String cantidadsolicitados = objsnapshot.child("cantidadProductosSolicitados").getValue().toString();
                        String cantidadDisponibles = objsnapshot.child("cantidadProductosDisponibles").getValue().toString();

                        consultarDetalleProducto(idProd,cantidadsolicitados, cantidadDisponibles);
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

    public void consultarDetalleProducto(String idProduct, String cantidadSolicitados, String cantidadDisponibles){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        subTotalCarrito=0.0;
        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    producto.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            assert p != null;
                            String id = p.getIdProducto();

                            Date fechaInicio = null;
                            Date fechaFin = null;
                            Date getCurrentDateTime = null;

                            if(idProduct.equals(id)) {
                                String estado = p.getEstadoProducto();
                                try {
                                    fechaInicio = sdf.parse(p.getFechaInicio() + " " + p.getHoraInicio());
                                    fechaFin = sdf.parse(p.getFechaFin() + " " + p.getHoraFin());
                                    getCurrentDateTime = c.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (getCurrentDateTime.compareTo(fechaInicio) > 0 && getCurrentDateTime.compareTo(fechaFin) < 0
                                        && !estado.equals("Cancelado por el vendedor")) {
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(),
                                            Integer.parseInt(cantidadSolicitados), Integer.parseInt(cantidadDisponibles), p.getPrecioDomicilio(),
                                            p.getIdVendedor()));
                                    subTotalCarrito = subTotalCarrito + (p.getPrecio() - p.getDescuento()) * Integer.parseInt(cantidadSolicitados);
                                }
                            }
                        }
                    }
                }
                ListShoppingCartAdapter = new listShoppingCartAdapter(shoppingCart.this, listaDeDatos, shoppingCart.this);
                listado.setAdapter(ListShoppingCartAdapter);
                String patron = "###,###.##";
                DecimalFormat objDF = new DecimalFormat (patron);
                totalCarrito.setText("Sub Total: $" + objDF.format(subTotalCarrito));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}