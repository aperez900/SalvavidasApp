package com.desarollo.salvavidasapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListSellAdapter extends RecyclerView.Adapter<ListSellAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<Productos> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRefUsuarios;
    ListDirecciones d;

    public ListSellAdapter(Context context, ArrayList<Productos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRefUsuarios = database.getReference("usuarios");
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_de_lista,null,false);
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ListSellAdapter.viewHolder holder, int position) {

        String idProducto = listaDeDatos.get(position).getIdProducto();
        String tipoProducto = listaDeDatos.get(position).getCategoriaProducto();
        String nombreProducto = listaDeDatos.get(position).getNombreProducto();
        String domicilioProducto = listaDeDatos.get(position).getDomicilio();
        String descripcionProducto = listaDeDatos.get(position).getDescripcionProducto();
        int cantidadProducto = listaDeDatos.get(position).getCantidad();
        int cantidadProductosDisponinles = listaDeDatos.get(position).getCantidadDisponible();
        Double precioProducto = listaDeDatos.get(position).getPrecio();
        Double descuentoProducto = listaDeDatos.get(position).getDescuento();
        long porcDescuento = Math.round(descuentoProducto/precioProducto*100);
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String horaInicio = listaDeDatos.get(position).getHoraInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String horaFin = listaDeDatos.get(position).getHoraFin();
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        String direccionProducto = listaDeDatos.get(position).getDireccion();
        consultarDireccionUsuario(direccionProducto, holder);
        Double precioDomicilio = listaDeDatos.get(position).getPrecioDomicilio();
        String patron = "###,###.##";
        DecimalFormat objDF = new DecimalFormat (patron);
        String nombreEstablecimiento = listaDeDatos.get(position).getNombreEmpresa();
        String idVendedor = listaDeDatos.get(position).getIdVendedor();

        holder.nombre_producto.setText(nombreProducto);
        holder.precio.setText(objDF.format(precioProducto-descuentoProducto));
        holder.porcentajeDescuento.setText(String.valueOf(-porcDescuento));
        holder.fechaInicio.setText(fechaInicio);
        holder.fechaFin.setText(fechaFin);
        holder.nombre_empresa.setText(nombreEstablecimiento);

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);

        holder.imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irADetalleDeProducto(nombreProducto, idProducto, tipoProducto, domicilioProducto, descripcionProducto,
                        cantidadProducto, cantidadProductosDisponinles, precioProducto, descuentoProducto, precioDomicilio, fechaInicio, horaInicio,
                        fechaFin, horaFin, getUrlFoto, idVendedor);
            }
        });
    }

    private void irADetalleDeProducto(String nombreProducto, String idProducto, String tipoProducto, String domicilioProducto,
                                      String descripcionProducto, int cantidadProducto, int cantidadProductosDisponibles, Double precioProducto, Double descuentoProducto,
                                      Double precioDomicilio, String fechaInicio, String horaInicio, String fechaFin,
                                      String horaFin, String getUrlFoto, String idVendedor){
        Intent intent = new Intent(activity , lookAtProduct.class);
        intent.putExtra("nombreProducto", nombreProducto);
        intent.putExtra("idProducto" , idProducto);
        intent.putExtra("tipoProducto" , tipoProducto);
        intent.putExtra("domicilioProducto" , domicilioProducto);
        intent.putExtra("descripcionProducto" , descripcionProducto);
        intent.putExtra("cantidadProducto" , String.valueOf(cantidadProducto));
        intent.putExtra("cantidadProductosDisponibles" , String.valueOf(cantidadProductosDisponibles));
        intent.putExtra("precio" , String.valueOf(precioProducto));
        intent.putExtra("descuento" , String.valueOf(descuentoProducto));
        intent.putExtra("precioDomicilio" , String.valueOf(precioDomicilio));
        intent.putExtra("fechaInicio", fechaInicio);
        intent.putExtra("horaInicio", horaInicio);
        intent.putExtra("fechaFin", fechaFin);
        intent.putExtra("horaFin", horaFin);
        intent.putExtra("getUrlFoto" , getUrlFoto);
        intent.putExtra("idVendedor" , idVendedor);
        intent.putExtra("tipyEntry" , "Consultar");
        activity.startActivity(intent);
    }

    private void consultarDireccionUsuario(String direccionProducto, viewHolder holder){
        final String[] Distancia = {""};
        myRefUsuarios.child(currentUser.getUid()).child("mis_direcciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot objsnapshot : snapshot.getChildren()){
                        d = new ListDirecciones();
                        d = objsnapshot.getValue(ListDirecciones.class);
                        if(d.getSeleccion().equals("true")){
                            DecimalFormat df = new DecimalFormat("0.00");
                            //Distancia[0] = df.format(calcularDistanciaEntreDirecciones(direccionProducto,d.direccionUsuario));
                            //holder.distancia.setText(Distancia[0] + " KM");
                        }
                    }
                }else{
                    //Toast.makeText(getApplicationContext(), "Registre al menos una dirección para poder calcular la distancia.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error consultando la dirección del usuario. Intente de nuevo mas tarde.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calcularDistanciaEntreDirecciones(String direccionProducto, String direccionUsuario){
        Geocoder geocoder =  new Geocoder(getApplicationContext(), Locale.getDefault());
        Location location = new Location("localizacion 1");
        Location location2 = new Location("localizacion 2");
        double distance = 0;

        try {
            List<Address> addresses = geocoder.getFromLocationName(direccionProducto,1);
            List<Address> addresses_buy = geocoder.getFromLocationName(direccionUsuario,1);

            if (addresses.size() > 0 && addresses_buy.size() > 0) {
                double latitud = addresses.get(0).getLatitude();
                double longitud = addresses.get(0).getLongitude();
                double latitud_usuario = addresses_buy.get(0).getLatitude();
                double longitud_usuario = addresses_buy.get(0).getLongitude();
                location.setLatitude(latitud);
                location.setLongitude(longitud);
                location2.setLatitude(latitud_usuario);
                location2.setLongitude(longitud_usuario);
                distance = location.distanceTo(location2) / 1000;
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error calculando la distancia del producto. Inténtalo mas tarde",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return distance;
    }

    @Override
    public int getItemCount() {
        int a ;
        if(listaDeDatos != null && !listaDeDatos.isEmpty()) {
            a = listaDeDatos.size();
        }
        else {
            a = 0;
        }
        return a;
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView nombre_producto, precio, fechaInicio, fechaFin,
                porcentajeDescuento, nombre_empresa, distancia;
        ImageView imagenProducto,imgDisponibilidad;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_producto = itemView.findViewById(R.id.tv_nombre_producto);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            precio = itemView.findViewById(R.id.tv_total_producto);
            fechaInicio = itemView.findViewById(R.id.tv_fecha_inicio_producto);
            fechaFin = itemView.findViewById(R.id.tv_fecha_fin_producto);
            porcentajeDescuento = itemView.findViewById(R.id.tv_porc_descuento);
            nombre_empresa = itemView.findViewById(R.id.tv_nombre_empresa);
            distancia = itemView.findViewById(R.id.tv_distancia);
            imgDisponibilidad = itemView.findViewById(R.id.img_disponibilidad);
        }
    }
}
