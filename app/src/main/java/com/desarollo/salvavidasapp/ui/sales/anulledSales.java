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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class anulledSales extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView tituloAnulados, subtituloAnulados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anulled_sales);

        tituloAnulados = findViewById(R.id.tv_titulo_anulado);
        subtituloAnulados = findViewById(R.id.tv_subtitulo_anulado);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoCancelados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
        listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(this,listaDeDatos, anulledSales.this);
        listado.setAdapter(listSellAdapter);

        crearListado();
    }

    private void crearListado() {

        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy");

        myRef
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            listaDeDatos.clear();
                            for(DataSnapshot objsnapshot : snapshot.getChildren()){
                                Productos p = objsnapshot.getValue(Productos.class);
                                Date fecha = null;
                                Date hoy = new Date();
                                String estado="";
                                try {
                                    fecha = fechaHora.parse(p.getFechaInicio());
                                    estado = p.getEstadoProducto();
                                    //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (estado.equals("Anulado")){
                                    // Toast.makeText(getContext(), p.getFechaInicio(), Toast.LENGTH_SHORT).show();
                                    tituloAnulados.setText("Productos cancelados");
                                    subtituloAnulados.setText("Los siguientes productos ya no estan disponibles para la venta");
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), Math.round(p.getPrecio()), Math.round(p.getDescuento()), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(),1,p.getCantidadDisponible(),p.getPrecioDomicilio(),
                                            p.getIdVendedor()));
                                }
                            }
                            listSellAdapter = new ListSellAdapter(anulledSales.this,listaDeDatos, anulledSales.this);
                            listado.setAdapter(listSellAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(anulledSales.this, "Error cargando los productos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
