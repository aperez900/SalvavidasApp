package com.desarollo.salvavidasapp.ui.sales;

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
import com.desarollo.salvavidasapp.ui.home.HomeViewModel;
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

import static com.facebook.FacebookSdk.getApplicationContext;


public class salesOffered extends Fragment {


    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    RecyclerView listado;
    ListSaleOffered listSaleOffered;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public salesOffered() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sales_offered,container,false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("productos");

        listado = view.findViewById(R.id.listado);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listado.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSaleOffered = new ListSaleOffered(listaDeDatos,getActivity());
        listado.setAdapter(listSaleOffered);

        crearListado();
        return view;
    }


    private void crearListado() {

        DateFormat fechaHora = new SimpleDateFormat("dd/MM/yyyy");


        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (fecha.before(hoy) &&  !estado.equals("Cancelado")){

                            //Toast.makeText(getContext(), p.getFechaInicio(), Toast.LENGTH_SHORT).show();
                            listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                    p.getCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                    p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin()));

                        }
                    }
                    listSaleOffered = new ListSaleOffered(listaDeDatos,getActivity());
                    listado.setAdapter(listSaleOffered);
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
