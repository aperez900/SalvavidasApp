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

public class purchases_made extends AppCompatActivity {
    TextView titulo_compras_realizadas, subtitulo_compras_realizadas;
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

        titulo_compras_realizadas = findViewById(R.id.tv_titulo_compras_realizadas);
        subtitulo_compras_realizadas = findViewById(R.id.tv_subtitulo_compras_realizadas);

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

        myRefVendedores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtitulo_compras_realizadas.setText("Se realizo la compra de los siguientes productos");
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los productos
                        for(DataSnapshot objsnapshot2 : objsnapshot.child("productos_en_tramite").getChildren()) {
                            String test = objsnapshot2.getKey();
                            if (test.equals(currentUser.getUid())){
                                for(DataSnapshot objsnapshot3 : objsnapshot2.getChildren()){
                                    String idProd = objsnapshot3.child("idProducto").getValue().toString();
                                    String cantidad = objsnapshot3.child("cantidadProducto").getValue().toString();
                                    String idUsuarioSolicitud = objsnapshot3.child("usuarioSolicitud").getValue().toString();
                                    String estadoSolicitud = objsnapshot3.child("estado").getValue().toString();
                                    String valorCompra = objsnapshot3.child("valorProducto").getValue().toString();
                                    //consultarDatosUsuario(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud);
                                    if (estadoSolicitud.equals("Realizado")) {
                                        consultarDetalleProducto(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud, Double.parseDouble(valorCompra));
                                    }else{
                                    }

                                }
                            }
                        }
                    }
                }
                else {
                    Intent intent = new Intent(purchases_made.this, Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(purchases_made.this, "Aún no se ha realizado compras", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(purchases_made.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDetalleProducto(String idProduct, String cantidad, String correoUsuarioSolicitud, String estadoSolicitud, double valorCompra){

        myRefProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            ProductosEnTramite p = objsnapshot2.getValue(ProductosEnTramite.class);
                            String id = p.getIdProducto();
                            if(idProduct.equals(id)) {
                                listaDeDatos.add(new ProductosEnTramite(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), valorCompra, p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(), p.getNombreEmpresa(), p.getDireccion(),
                                        Integer.parseInt(cantidad),p.getPrecioDomicilio(), p.getIdVendedor(), currentUser.getUid(), currentUser.getDisplayName(),
                                        correoUsuarioSolicitud, estadoSolicitud));
                            }
                        }
                    }
                }
                ListPurchasesInProcessAdapter = new listPurchasesInProcessAdapter(purchases_made.this, listaDeDatos, purchases_made.this);
                listadoComprasRealizadas.setAdapter(ListPurchasesInProcessAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}