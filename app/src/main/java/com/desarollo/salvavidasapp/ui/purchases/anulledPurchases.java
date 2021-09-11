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

public class anulledPurchases extends AppCompatActivity {

    TextView tituloComprasAnuladas, subtituloComprasAnuladas;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios, myRefVendedores, myRefProductos;
    RecyclerView listadoComprasAnuladas;

    listPurchasesInProcessAdapter ListPurchasesAnulledAdapter;
    ArrayList<ProductosEnTramite> listaDeDatos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anulled_purchases);

        tituloComprasAnuladas = findViewById(R.id.tv_titulo_compras_anuladas);
        subtituloComprasAnuladas = findViewById(R.id.tv_subtitulo_compras_anuladas);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefVendedores = database.getReference("vendedores");
        myRefProductos = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");

        listadoComprasAnuladas = findViewById(R.id.listado_compras_anuladas);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listadoComprasAnuladas.setLayoutManager(manager);
//        listado.setHasFixedSize(true);

        ListPurchasesAnulledAdapter = new listPurchasesInProcessAdapter(this, listaDeDatos, this);
        listadoComprasAnuladas.setAdapter(ListPurchasesAnulledAdapter);

        crearListado();
    }

    public void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRefUsuarios.child(currentUser.getUid()).child("mis_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subtituloComprasAnuladas.setText("Los siguientes productos fueron anulados");
                    listaDeDatos.clear();
                    for (DataSnapshot objsnapshot : snapshot.getChildren()) { //recorre los productos
                        String estadoSolicitud = objsnapshot.child("estado").getValue().toString();
                        if (estadoSolicitud.equals("Anulado por el comprador")){
                            String idProd = objsnapshot.child("idProducto").getValue().toString();
                            String cantidad = objsnapshot.child("cantidadProducto").getValue().toString();
                            String idUsuarioSolicitud = objsnapshot.child("usuarioSolicitud").getValue().toString();
                            String valorCompra = objsnapshot.child("valorProducto").getValue().toString();
                            consultarDetalleProducto(idProd, cantidad, idUsuarioSolicitud, estadoSolicitud, Double.parseDouble(valorCompra));
                        }
                    }
                }
                else {
                    Intent intent = new Intent(anulledPurchases.this, Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(anulledPurchases.this, "Aún no se han anulado compras", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(anulledPurchases.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
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
                ListPurchasesAnulledAdapter = new listPurchasesInProcessAdapter(anulledPurchases.this, listaDeDatos, anulledPurchases.this);
                listadoComprasAnuladas.setAdapter(ListPurchasesAnulledAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}