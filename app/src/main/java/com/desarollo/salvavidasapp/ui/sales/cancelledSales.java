package com.desarollo.salvavidasapp.ui.sales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class cancelledSales extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView tituloCancelados, subtituloCancelados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_sales);

        tituloCancelados = findViewById(R.id.tv_titulo_cancelados);
        subtituloCancelados = findViewById(R.id.tv_subtitulo_cancelados);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoCancelados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(this,listaDeDatos, cancelledSales.this);
        listado.setAdapter(listSellAdapter);

        crearListado();
    }

    private void crearListado() {

        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy");

        myRef.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            listaDeDatos.clear();
                            for(DataSnapshot objsnapshot : snapshot.getChildren()){
                                Productos p = objsnapshot.getValue(Productos.class);
                                String estado="";
                                estado = p.getEstadoProducto();
                                if (estado.equals("Cancelado por el vendedor")){
                                    tituloCancelados.setText("Productos cancelados");
                                    subtituloCancelados.setText("Los siguientes productos ya no estan disponibles para la venta");
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), Math.round(p.getPrecio()), Math.round(p.getDescuento()), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(),1,p.getPrecioDomicilio(),
                                            p.getIdVendedor()));
                                }
                            }
                            listSellAdapter = new ListSellAdapter(cancelledSales.this,listaDeDatos, cancelledSales.this);
                            listado.setAdapter(listSellAdapter);
                            /*
                            //Acciones al dar clic en un item de la lista
                            listSellAdapter.setOnClickListener(view -> {
                                String idProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getIdProducto();
                                String nombreProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getNombreProducto();
                                String descripcionProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getDescripcionProducto();
                                String categoriaProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getCategoriaProducto();
                                double precioProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getPrecio();
                                double descuentoProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getDescuento();
                                String domicilioProducto = listaDeDatos.get(listado.getChildAdapterPosition(view)).getDomicilio();
                                String fechaInicio = listaDeDatos.get(listado.getChildAdapterPosition(view)).getFechaInicio();
                                String fechaFin = listaDeDatos.get(listado.getChildAdapterPosition(view)).getFechaFin();
                                String horaInicio = listaDeDatos.get(listado.getChildAdapterPosition(view)).getHoraInicio();
                                String horaFin = listaDeDatos.get(listado.getChildAdapterPosition(view)).getHoraFin();

                                Intent intent = new Intent(cancelledSales.this, addProduct.class);
                                intent.putExtra("idProducto", idProducto);
                                intent.putExtra("nombreProducto", nombreProducto);
                                intent.putExtra("descripcionProducto", descripcionProducto);
                                intent.putExtra("categoriaProducto", categoriaProducto);
                                intent.putExtra("precioProducto", String.valueOf(precioProducto));
                                intent.putExtra("descuentoProducto", String.valueOf(descuentoProducto));
                                intent.putExtra("domicilioProducto", domicilioProducto);
                                intent.putExtra("fechaInicio", fechaInicio);
                                intent.putExtra("fechaFin", fechaFin);
                                intent.putExtra("horaInicio", horaInicio);
                                intent.putExtra("horaFin", horaFin);
                                startActivity(intent);
                            });
                             */
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(cancelledSales.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}