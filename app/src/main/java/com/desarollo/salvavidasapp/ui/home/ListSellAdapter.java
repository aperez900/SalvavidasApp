package com.desarollo.salvavidasapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.lookAtProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    DatabaseReference myRef;


    public ListSellAdapter(Context context, ArrayList<Productos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
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
        String id_producto = listaDeDatos.get(position).getIdProducto();
        String tipo_producto = listaDeDatos.get(position).getCategoriaProducto();
        String nombre_producto = listaDeDatos.get(position).getNombreProducto();
        String descripcion_producto = listaDeDatos.get(position).getDescripcionProducto();
        Double precio = listaDeDatos.get(position).getPrecio();
        Double descuento = listaDeDatos.get(position).getDescuento();
        long porcDescuento = Math.round(descuento/precio*100);
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        DecimalFormat df = new DecimalFormat("#,00");
        double aleatorio = Math.random()*5;
        String Distancia = df.format(aleatorio);
        holder.nombre_producto.setText(nombre_producto);
        String patron = "###. ###, ##";
        DecimalFormat objDF = new DecimalFormat (patron);
        holder.precio.setText(objDF.format(precio-descuento));
        //holder.precio.setText(String.valueOf(precio-descuento));
        holder.porcentajeDescuento.setText(String.valueOf(-porcDescuento));
        holder.fechaInicio.setText(fechaInicio);
        holder.fechaFin.setText(fechaFin);
        holder.nombre_empresa.setText("Nombre empresa");
        holder.distancia.setText(Distancia + " KM");

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);


        holder.imagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , lookAtProduct.class);
                intent.putExtra("nombreProducto", listaDeDatos.get(position).getNombreProducto());
                intent.putExtra("idProducto" , listaDeDatos.get(position).getIdProducto());
                intent.putExtra("tipoProducto" , listaDeDatos.get(position).getCategoriaProducto());
                intent.putExtra("domicilioProducto" , listaDeDatos.get(position).getDomicilio());
                intent.putExtra("descripcionProducto" , listaDeDatos.get(position).getDescripcionProducto());
                intent.putExtra("precio" , String.valueOf(listaDeDatos.get(position).getPrecio()));
                intent.putExtra("descuento" , String.valueOf(listaDeDatos.get(position).getDescuento()));
                intent.putExtra("fechaInicio", listaDeDatos.get(position).getFechaInicio());
                intent.putExtra("horaInicio", listaDeDatos.get(position).getHoraInicio());
                intent.putExtra("fechaFin", listaDeDatos.get(position).getFechaFin());
                intent.putExtra("horaFin", listaDeDatos.get(position).getHoraFin());
                intent.putExtra("getUrlFoto" , listaDeDatos.get(position).getfoto());
                intent.putExtra("tipyEntry" , "Consultar");

                activity.startActivity(intent);
            }
        });


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
        TextView tipo_producto, nombre_producto, descripcion_producto, precio, fechaInicio, fechaFin,
                porcentajeDescuento, nombre_empresa, distancia;
        ImageView imagenProducto, imgVer, imgEditar, imgCancelar;

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
        }

    }

}
