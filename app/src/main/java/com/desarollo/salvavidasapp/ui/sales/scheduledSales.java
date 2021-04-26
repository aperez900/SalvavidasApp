package com.desarollo.salvavidasapp.ui.sales;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import static com.facebook.FacebookSdk.getApplicationContext;

public class scheduledSales extends Fragment {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSellAdapter listSellAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public scheduledSales() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scheduled_sales, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = view.findViewById(R.id.listado);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listado.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getContext(),listaDeDatos,getActivity());
        listado.setAdapter(listSellAdapter);

        crearListado();

        return view;
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
                        if (fecha.after(hoy) && !estado.equals("Cancelado")){

                           // Toast.makeText(getContext(), p.getFechaInicio(), Toast.LENGTH_SHORT).show();
                            listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                    p.getCategoriaProducto(), Math.round(p.getPrecio()), Math.round(p.getDescuento()), p.getDomicilio(), p.getEstadoProducto(),
                                    p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin()));
                        }
                      }
                    listSellAdapter = new ListSellAdapter(getContext(),listaDeDatos,getActivity());
                    listado.setAdapter(listSellAdapter);
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
                        Intent intent = new Intent(getContext(), editProduct.class);
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
                }else{
                    //Valores por defecto
                    listaDeDatos.add(new Productos("", "Nombre producto", "Descipción producto",
                            "Categoría producto", 0, 0, "Domicilio Sí/No", "Estado producto",
                            "", "Fecha inicio", "Hora inicio", "Fecha fin", "Hora fin"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}