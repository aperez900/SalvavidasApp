package com.desarollo.salvavidasapp.ui.purchases;

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

public class purchasesMade extends AppCompatActivity {
    TextView tituloComprasRealizadas, subtituloComprasRealizadas;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios, myRefVendedores, myRefProductos;
    RecyclerView listadoComprasRealizadas;

    listPurchasesInProcessAdapter ListPurchasesInProcessAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_made);

        tituloComprasRealizadas = findViewById(R.id.tv_titulo_compras_realizadas);
        subtituloComprasRealizadas = findViewById(R.id.tv_subtitulo_compras_realizadas);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");

        listadoComprasRealizadas = findViewById(R.id.listado_compras_realizadas);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listadoComprasRealizadas.setLayoutManager(manager);
//        listado.setHasFixedSize(true);

        ListPurchasesInProcessAdapter = new listPurchasesInProcessAdapter(this, listaDeDatos, this);
        listadoComprasRealizadas.setAdapter(ListPurchasesInProcessAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        TimerTask inicio = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(purchasesMade.this, Home.class);
                startActivity(intent);
                finish();
            }
        };

        myRefUsuarios.child(currentUser.getUid()).child("mis_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtituloComprasRealizadas.setText("Se realizo la compra de los siguientes productos");
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre las compras
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) { //recorre los productos
                        String estadoSolicitud = objsnapshot2.child("estado").getValue().toString();
                            if (estadoSolicitud.equals("Realizado") || estadoSolicitud.equals("Pagado")) {
                                String idProd = objsnapshot2.child("idProducto").getValue().toString();
                                String idCompra = objsnapshot2.child("idCompra").getValue().toString();
                                String cantidad = objsnapshot2.child("cantidadProducto").getValue().toString();
                                String idUsuarioSolicitud = objsnapshot2.child("usuarioSolicitud").getValue().toString();
                                String valorCompra = objsnapshot2.child("valorProducto").getValue().toString();
                                consultarDetalleProducto(idProd, idCompra, cantidad, idUsuarioSolicitud, estadoSolicitud, Double.parseDouble(valorCompra));
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(purchasesMade.this, "Aún no se ha realizado compras", Toast.LENGTH_SHORT).show();

                    Timer tiempo = new Timer();
                    tiempo.schedule(inicio,1500);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(purchasesMade.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String idCompra, String cantidad, String correoUsuarioSolicitud, String estadoSolicitud, double valorCompra){

        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            ProductosEnTramite p = objsnapshot2.getValue(ProductosEnTramite.class);
                            String id = p.getIdProducto();
                            if(idProduct.equals(id)) {
                                listaDeDatos.add(new ProductosEnTramite(p.getIdProducto(), idCompra, p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), valorCompra, p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(),
                                        Integer.parseInt(cantidad), p.getCantidadDisponible(),p.getPrecioDomicilio(), p.getIdVendedor(), currentUser.getUid(), currentUser.getDisplayName(),
                                        correoUsuarioSolicitud, estadoSolicitud));
                            }
                        }
                    }
                }
                ListPurchasesInProcessAdapter = new listPurchasesInProcessAdapter(purchasesMade.this, listaDeDatos, purchasesMade.this);
                listadoComprasRealizadas.setAdapter(ListPurchasesInProcessAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}