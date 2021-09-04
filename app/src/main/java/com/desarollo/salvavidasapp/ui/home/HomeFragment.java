package com.desarollo.salvavidasapp.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.Models.TipoComidas;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {

    ArrayList<Productos> listaDeDatos = new ArrayList<>();
    ArrayList<TipoComidas> listaDeDatosTipo = new ArrayList<>();
    RecyclerView listado_comidas,listado_tipo_comidas;
    ListSellAdapter listSellAdapter;
    ListTypeFood listTypeFood;
    ProgressDialog cargando;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefTypeFood,myRef,myRefVendedores,myRefUsuarios;
    ListDirecciones d;
    TextView tv_principal_address;
    Button btnShopping;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefTypeFood = database.getReference("tipo_comidas");
        myRef = database.getReference("productos");
        myRefUsuarios = database.getReference("usuarios");
        myRefVendedores = database.getReference("vendedores");
        TextView tv_saludo = view.findViewById(R.id.tv_saludo_home);
        tv_principal_address  = view.findViewById(R.id.principal_address);
        btnShopping = view.findViewById(R.id.btnShopping);
        cargando = new ProgressDialog(getActivity());
        listado_comidas = view.findViewById(R.id.listado);
        listado_tipo_comidas = view.findViewById(R.id.tipo_comidas);
        listado_tipo_comidas.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo,getActivity());
        listado_tipo_comidas.setAdapter(listTypeFood);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listado_comidas.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, getActivity());
        listado_comidas.setAdapter(listSellAdapter);

        consultarDireccionUsuario();
        actualizarNombreUsuario(tv_saludo);
        crearListadoTiposDeProductos();
        crearListadoProductos();

        tv_principal_address.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_address));

        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAlCarritoDeCompras();
            }
        });

        return view;
    }

    private void irAlCarritoDeCompras(){
        Intent intent = new Intent(getApplicationContext(), shoppingCart.class);
        startActivity(intent);
    }

    private void actualizarNombreUsuario(TextView tv_saludo){
        if(currentUser != null) {
            String nombreCompleto = currentUser.getDisplayName();
            String  [] primerNombre = nombreCompleto.split(" ");
            tv_saludo.setText("Hola "+ primerNombre[0] +" ¿Qué vas a pedir hoy?");
        }
    }

    private void crearListadoTiposDeProductos() {
        myRefTypeFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatosTipo.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        TipoComidas t = objsnapshot.getValue(TipoComidas.class);
                        assert t != null;
                        listaDeDatosTipo.add(new TipoComidas(t.getTipoComida(),t.getFoto()));
                        }
                    listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo, getActivity());
                    listado_tipo_comidas.setAdapter(listTypeFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearListadoProductos() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        cargando.setTitle("Cargando");
        cargando.setMessage("Un momento por favor...");
        cargando.show();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatos.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        for(DataSnapshot objsnapshot2 : objsnapshot.getChildren()){ //recorre los productos
                            Productos p = objsnapshot2.getValue(Productos.class);
                            String estado;
                            Date fechaInicio = null;
                            Date fechaFin = null;
                            Date getCurrentDateTime = null;
                            try {
                                assert p != null;
                                fechaInicio = sdf.parse(p.getFechaInicio() + " " + p.getHoraInicio());
                                fechaFin = sdf.parse(p.getFechaFin() + " " + p.getHoraFin());
                                getCurrentDateTime = c.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            estado = p.getEstadoProducto();
                            assert getCurrentDateTime != null;
                            if (getCurrentDateTime.compareTo(fechaInicio) > 0 && getCurrentDateTime.compareTo(fechaFin) < 0
                                    && !estado.equals("Cancelado por el vendedor")){

                                listaDeDatos.add(new Productos(p.getIdProducto(), p.getNombreProducto(), p.getDescripcionProducto(),
                                        p.getCategoriaProducto(), p.getSubCategoriaProducto(), p.getPrecio(), p.getDescuento(), p.getDomicilio(), p.getEstadoProducto(),
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(), 1,
                                        p.getPrecioDomicilio(), p.getIdVendedor()));
                            }
                        }
                    }
                    listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, getActivity());
                    listado_comidas.setAdapter(listSellAdapter);
                    cargando.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error cargando los productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultarDireccionUsuario(){
        myRefUsuarios.child(currentUser.getUid()).child("mis direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        d = new ListDirecciones();
                        d = objsnapshot.getValue(ListDirecciones.class);
                        if(d.getSeleccion().equals("true")){
                            tv_principal_address.setText(d.direccionUsuario);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error consultando las direcciones. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}