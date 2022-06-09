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
import com.desarollo.salvavidasapp.Models.TipoProductos;
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
    ArrayList<TipoProductos> listaDeDatosTipo = new ArrayList<>();
    RecyclerView listadoProducto, listadoTipoProducto;
    ListSellAdapter listSellAdapter;
    ListTypeFood listTypeFood;
    ProgressDialog cargando;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefTypeFood,myRef,myRefVendedores,myRefUsuarios;
    ListDirecciones d;
    TextView tvPrincipalAddress;
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
        tvPrincipalAddress = view.findViewById(R.id.principal_address);
        btnShopping = view.findViewById(R.id.btnShopping);
        cargando = new ProgressDialog(getActivity());
        listadoProducto = view.findViewById(R.id.listado);
        listadoTipoProducto = view.findViewById(R.id.tipo_comidas);
        listadoTipoProducto.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo,getActivity());
        listadoTipoProducto.setAdapter(listTypeFood);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        listadoProducto.setLayoutManager(manager);
        //listado.setHasFixedSize(true);
        listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, getActivity());
        listadoProducto.setAdapter(listSellAdapter);

        consultarDireccionUsuario();
        actualizarNombreUsuario(tv_saludo);
        crearListadoTiposDeProductos();
        crearListadoProductos();
        crearListado();

        tvPrincipalAddress.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_nav_home_to_nav_address));

        btnShopping.setOnClickListener(v -> irAlCarritoDeCompras());

        return view;
    }

    private void irAlCarritoDeCompras(){
        Intent intent = new Intent(getApplicationContext(), shoppingCart.class);
        startActivity(intent);
    }

    private void actualizarNombreUsuario(TextView tv_saludo){
        if(currentUser != null) {
            if(currentUser.getDisplayName() != null){
                String nombreCompleto = currentUser.getDisplayName();
                String  [] primerNombre = nombreCompleto.split(" ");
                tv_saludo.setText("Hola "+ primerNombre[0] +" ¿Qué vas a pedir hoy?");
            }
            tv_saludo.setText("Hola ¿Qué vas a pedir hoy?");

        }
    }

    private void crearListadoTiposDeProductos() {
        myRefTypeFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    listaDeDatosTipo.clear();
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){ //Recorre los usuarios
                        TipoProductos t = objsnapshot.getValue(TipoProductos.class);
                        assert t != null;
                        listaDeDatosTipo.add(new TipoProductos(t.getTipoComida(),t.getFoto()));
                        }
                    listTypeFood = new ListTypeFood(getContext(),listaDeDatosTipo, getActivity());
                    listadoTipoProducto.setAdapter(listTypeFood);
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
                                        p.getfoto(), p.getFechaInicio(), p.getHoraInicio(), p.getFechaFin(), p.getHoraFin(),p.getNombreEmpresa(),p.getDireccion(), p.getCantidad(),
                                        p.getCantidadDisponible(), p.getPrecioDomicilio(), p.getIdVendedor()));
                            }
                        }
                    }
                    listSellAdapter = new ListSellAdapter(getApplicationContext(),listaDeDatos, getActivity());
                    listadoProducto.setAdapter(listSellAdapter);
                    cargando.dismiss();
                }
                cargando.dismiss();
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
                            tvPrincipalAddress.setText(d.direccionUsuario);
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

    public void crearListado() {

        myRefUsuarios.child(currentUser.getUid()).child("carrito_compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    btnShopping.setVisibility(View.VISIBLE);
                }else{
                    btnShopping.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}