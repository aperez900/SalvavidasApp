package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.desarollo.salvavidasapp.Models.Productos;
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

public class salesMade extends AppCompatActivity {
    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listadoVentasEfectuadas;
    ListSaleAdapter listSaleAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefVendedores,myRefProductos;
    TextView tituloVentasEfectuadas, subtituloVentasEfectuadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_made);

        tituloVentasEfectuadas = findViewById(R.id.tv_titulo_ventas_efectuadas);
        subtituloVentasEfectuadas = findViewById(R.id.tv_subtitulo_ventas_efectuadas);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");

        listadoVentasEfectuadas = findViewById(R.id.listadoVentasEfectuadas);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listadoVentasEfectuadas.setLayoutManager(manager);
        listSaleAdapter = new ListSaleAdapter(this, listaDeDatos,this);
        listadoVentasEfectuadas.setAdapter(listSaleAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRefVendedores.child(currentUser.getUid()).child("productos_en_tramite").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtituloVentasEfectuadas.setText("Se han comprado los siguientes productos");
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los usuarios
                        for (DataSnapshot objsnapshot2 : objsnapshot.getChildren()) {
                            for (DataSnapshot objsnapshot3 : objsnapshot2.getChildren()) { //recorre los productos
                                String idProd = objsnapshot3.child("idProducto").getValue().toString();
                                String cantidad = objsnapshot3.child("cantidadProducto").getValue().toString();
                                String idUsuarioSolicitud = objsnapshot3.child("usuarioSolicitud").getValue().toString();
                                String estadoSolicitud = objsnapshot3.child("estado").getValue().toString();
                                String valorCompra = objsnapshot3.child("valorProducto").getValue().toString();
                                //consultarDatosUsuario(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud);
                                if (estadoSolicitud.equals("Pagado")) {
                                    consultarDetalleProducto(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud, Double.parseDouble(valorCompra));
                                }
                            }
                        }
                    }
                }
                else {
                    Intent intent = new Intent(salesMade.this, Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(salesMade.this, "Aún no se han realizado compras", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(salesMade.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
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
                                listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(),1, p.getCantidadDisponible(),p.getPrecioDomicilio(),
                                        p.getIdVendedor()));
                            }
                        }
                    }
                }
                listSaleAdapter = new ListSaleAdapter(salesMade.this, listaDeDatos, salesMade.this);
                listadoVentasEfectuadas.setAdapter(listSaleAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}