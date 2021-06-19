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
import java.util.Calendar;
import java.util.Date;

public class scheduledSales extends AppCompatActivity {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSaleAdapter listSaleAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_sales);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = findViewById(R.id.listadoProgramados);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        listado.setLayoutManager(manager);
//        listado.setHasFixedSize(true);
        listSaleAdapter = new ListSaleAdapter(this,listaDeDatos, this);
        listado.setAdapter(listSaleAdapter);

        crearListado();
    }

    private void crearListado() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        myRef
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            listaDeDatos.clear();
                            for(DataSnapshot objsnapshot : snapshot.getChildren()){
                                Productos p = objsnapshot.getValue(Productos.class);
                                String estado="";
                                Date fecha = null;
                                Date getCurrentDateTime = null;
                                try {
                                    fecha = sdf.parse(p.getFechaInicio() + " " + p.getHoraInicio());
                                    getCurrentDateTime = c.getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                estado = p.getEstadoProducto();
                                if (getCurrentDateTime.compareTo(fecha) < 0 &&  !estado.equals("Cancelado")) {

                                    //Toast.makeText(scheduledSales.this, getCurrentDateTime + " - " + fecha + " - " + getCurrentDateTime.compareTo(fecha) , Toast.LENGTH_SHORT).show();
                                    tv1.setText("Productos programados");
                                    tv2.setText("Los siguientes productos estarán disponibles para la venta una vez se cumpla su fecha y hora de inicio");
                                    listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                            p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                            p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(),1,p.getPrecioDomicilio()));

                                }

                            }
                            listSaleAdapter = new ListSaleAdapter(scheduledSales.this,listaDeDatos, scheduledSales.this);
                            listado.setAdapter(listSaleAdapter);

                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}