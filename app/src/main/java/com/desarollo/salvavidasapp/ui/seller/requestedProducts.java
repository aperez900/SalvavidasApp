package com.desarollo.salvavidasapp.ui.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.purchases.purchasesInProcess;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class requestedProducts extends AppCompatActivity {

    TextView titulo_productos_solicitados, subtitulo_productos_solicitados;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios, myRefVendedores, myRefProductos;
    RecyclerView listado, listadoHistorial;

    listRequestedProductsAdapter ListRequestedProductsAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();
    ArrayList<ProductosEnTramite> listaDeDatosHistorial = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_products);

        titulo_productos_solicitados = findViewById(R.id.tv_titulo_productos_solicitados);
        subtitulo_productos_solicitados = findViewById(R.id.tv_subtitulo_productos_solicitados);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");

        listado = findViewById(R.id.listado__productos_solicitados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        ListRequestedProductsAdapter = new listRequestedProductsAdapter(this, listaDeDatos, this);
        //listado.setHasFixedSize(true);
        listado.setAdapter(ListRequestedProductsAdapter);

        listadoHistorial = findViewById(R.id.listado_productos_solicitados_historial);

        listadoHistorial.setLayoutManager(manager2);
        ListRequestedProductsAdapter = new listRequestedProductsAdapter(this, listaDeDatosHistorial, this);
        listadoHistorial.setAdapter(ListRequestedProductsAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        TimerTask inicio = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(requestedProducts.this, Home.class);
                startActivity(intent);
                finish();
            }
        };

        myRefVendedores.child(currentUser.getUid()).child("productos_en_tramite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtitulo_productos_solicitados.setText("Los siguientes productos estan en trámite de aprobación o entrega");
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre las compras
                            for (DataSnapshot objsnapshot3 : objsnapshot2.getChildren()) { //recorre los productos
                                String idProd = objsnapshot3.child("idProducto").getValue().toString();
                                String idCompra = objsnapshot3.child("idCompra").getValue().toString();
                                String cantidad = objsnapshot3.child("cantidadProducto").getValue().toString();
                                String idUsuarioSolicitud = objsnapshot3.child("usuarioSolicitud").getValue().toString();
                                String estadoSolicitud = objsnapshot3.child("estado").getValue().toString();
                                consultarDatosUsuario(idProd, idCompra, cantidad, idUsuarioSolicitud, estadoSolicitud);
                            }
                        }
                    }
                } else {

                    Toast.makeText(requestedProducts.this, "Aún no han solicitado tus productos. Intenta de nuevo mas tarde.", Toast.LENGTH_SHORT).show();

                    Timer tiempo = new Timer();
                    tiempo.schedule(inicio,1500);

                                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requestedProducts.this, "Error cargando los productos del carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDatosUsuario(String idProd, String idCompra, String cantidad, String idUsuarioSolicitud, String estadoSolicitud) {

        myRefUsuarios.child(idUsuarioSolicitud).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombreUsuario = snapshot.child("nombre").getValue().toString();
                    String correoUsuario = snapshot.child("correo").getValue().toString();
                    consultarDetalleProducto(idProd, idCompra, cantidad, idUsuarioSolicitud, nombreUsuario, correoUsuario, estadoSolicitud);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando el nombre del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String idCompra, String cantidad, String idUsuarioSolicitud, String usuarioSolicitud, String correoUsuarioSolicitud, String estadoSolicitud){

        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            ProductosEnTramite p = objsnapshot2.getValue(ProductosEnTramite.class);
                            String id = p.getIdProducto();
                            if(idProduct.equals(id)) {
                                if(estadoSolicitud.equals("Solicitado")) {
                                    listaDeDatos.add(new ProductosEnTramite(p.getIdProducto(), idCompra, p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad),
                                            p.getCantidadDisponible() ,p.getPrecioDomicilio(),
                                            p.getIdVendedor(), idUsuarioSolicitud, usuarioSolicitud, correoUsuarioSolicitud, estadoSolicitud));
                                }else{
                                    listaDeDatosHistorial.add(new ProductosEnTramite(p.getIdProducto(), idCompra, p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(), Integer.parseInt(cantidad),
                                            p.getCantidadDisponible(),p.getPrecioDomicilio(),
                                            p.getIdVendedor(), idUsuarioSolicitud, usuarioSolicitud, correoUsuarioSolicitud, estadoSolicitud));
                                }
                            }
                        }
                    }
                }
                ListRequestedProductsAdapter = new listRequestedProductsAdapter(requestedProducts.this, listaDeDatos, requestedProducts.this);
                listado.setAdapter(ListRequestedProductsAdapter);

                ListRequestedProductsAdapter = new listRequestedProductsAdapter(requestedProducts.this, listaDeDatosHistorial, requestedProducts.this);
                listadoHistorial.setAdapter(ListRequestedProductsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}