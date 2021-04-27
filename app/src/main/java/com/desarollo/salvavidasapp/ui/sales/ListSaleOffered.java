package com.desarollo.salvavidasapp.ui.sales;

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
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListSaleOffered extends RecyclerView.Adapter<ListSaleOffered.viewHolder>  {

    ArrayList<Productos> listaDeDatos;
    Activity  activity;
    private View.OnClickListener listener;
    private  Context context;


    public ListSaleOffered(ArrayList<Productos> listaDeDatos, Activity activity) {
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ListSaleOffered.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vistaItemLista= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_de_lista,null,false);
        return new ListSaleOffered.viewHolder(vistaItemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSaleOffered.viewHolder holder, int position) {
        String id_producto = listaDeDatos.get(position).getIdProducto();
        String tipo_producto = listaDeDatos.get(position).getCategoriaProducto();
        String nombre_producto = listaDeDatos.get(position).getNombreProducto();
        String descripcion_producto = listaDeDatos.get(position).getDescripcionProducto();
        Double precio = listaDeDatos.get(position).getPrecio();
        Double descuento = listaDeDatos.get(position).getDescuento();
        String fechaInicio = listaDeDatos.get(position).getFechaInicio();
        String fechaFin = listaDeDatos.get(position).getFechaFin();
        String getUrlFoto = listaDeDatos.get(position).getfoto();
        holder.tipo_producto.setText(tipo_producto);
        holder.nombre_producto.setText(nombre_producto);
        holder.descripcion_producto.setText(descripcion_producto);
        holder.precio.setText(String.valueOf(precio-descuento));
        holder.fechaInicio.setText(fechaInicio);
        holder.fechaFin.setText(fechaFin);
        holder.actualizarDatosDeItem(listaDeDatos.get(position));
    }


    @Override
    public int getItemCount() {
        return listaDeDatos.size();
    }



    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tipo_producto, nombre_producto, descripcion_producto, precio, fechaInicio, fechaFin;
        ImageView imagenProducto, imgVer, imgEditar, imgCancelar;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            tipo_producto = itemView.findViewById(R.id.tv_tipo_producto);
            nombre_producto = itemView.findViewById(R.id.tv_nombre_producto);
            descripcion_producto = itemView.findViewById(R.id.tv_descripcion_producto);
            imagenProducto = itemView.findViewById(R.id.img_imagen_producto);
            precio = itemView.findViewById(R.id.tv_precio_producto);
            fechaInicio = itemView.findViewById(R.id.tv_fecha_inicio_producto);
            fechaFin = itemView.findViewById(R.id.tv_fecha_fin_producto);
            imgVer = itemView.findViewById(R.id.img_ver_mas_producto);
            imgEditar = itemView.findViewById(R.id.img_editar_producto);
            imgCancelar = itemView.findViewById(R.id.img_cancelar_producto);

        }

        public void actualizarDatosDeItem(Productos datos) {

            String getUrlFoto = datos.getfoto();

            Glide.with(activity)
                    .load(getUrlFoto)
                    .into(imagenProducto);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(),sales.class);
                    intent.putExtra("productos", listaDeDatos);
                    context.startActivity(intent);
                }
            });
        }
    }
}
