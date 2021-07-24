package com.desarollo.salvavidasapp.ui.purchases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Favoritos;
import com.desarollo.salvavidasapp.Models.ProductosEnTramite;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.Home;
import com.desarollo.salvavidasapp.ui.seller.listRequestedProductsAdapter;
import com.desarollo.salvavidasapp.ui.seller.requested_products;
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

public class purchasesInProcess extends AppCompatActivity {

    TextView titulo_compras_en_proceso, subtitulo_compras_en_proceso;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios, myRefVendedores, myRefProductos;
    RecyclerView listadoComprasEnProceso;

    listPurchasesInProcessAdapter ListPurchasesInProcessAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_in_process);

        titulo_compras_en_proceso = findViewById(R.id.tv_titulo_compras_en_proceso);
        subtitulo_compras_en_proceso = findViewById(R.id.tv_subtitulo_compras_en_proceso);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");

        listadoComprasEnProceso = findViewById(R.id.listado_compras_en_proceso);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listadoComprasEnProceso.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        ListPurchasesInProcessAdapter = new listPurchasesInProcessAdapter(this, listaDeDatos, this);
        listadoComprasEnProceso.setAdapter(ListPurchasesInProcessAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRefVendedores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtitulo_compras_en_proceso.setText("Los siguientes productos estan en proceso de compra");
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
                                    if (estadoSolicitud.equals("Cancelado")|| estadoSolicitud.equals("Realizado") || estadoSolicitud.equals("Anulado")) {
                                    }else{
                                        consultarDetalleProducto(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud, Double.parseDouble(valorCompra));
                                    }

                                }
                            }
                         }
                    }
                }
                else {
                    Intent intent = new Intent(purchasesInProcess.this, Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(purchasesInProcess.this, "Aún no tienes compras en proceso", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(purchasesInProcess.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
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
                ListPurchasesInProcessAdapter = new listPurchasesInProcessAdapter(purchasesInProcess.this, listaDeDatos, purchasesInProcess.this);
                listadoComprasEnProceso.setAdapter(ListPurchasesInProcessAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}