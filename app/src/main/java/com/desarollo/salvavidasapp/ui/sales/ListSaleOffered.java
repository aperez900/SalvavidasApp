package com.desarollo.salvavidasapp.ui.sales;

import android.content.Context;
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

    public ListSaleOffered(ArrayList<Productos> listaDeDatos) {
        this.listaDeDatos = listaDeDatos;
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
        holder.actualizarDatosDeItem(listaDeDatos.get(position));
    }


    @Override
    public int getItemCount() {
        return listaDeDatos.size();
    }

    TextView tipo_producto,nombre_producto,descripcion_producto;
    ImageView imagenProducto;

    public class viewHolder extends RecyclerView.ViewHolder {
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tipo_producto = itemView.findViewById(R.id.tv_tipo_producto);
            nombre_producto = itemView.findViewById(R.id.et_nombre_producto);
            descripcion_producto = itemView.findViewById(R.id.et_descripcion_producto);
           imagenProducto = itemView.findViewById(R.id.img_imagen_producto);

        }

        public void actualizarDatosDeItem(Productos datos) {

            tipo_producto.setText(datos.getCategoriaProducto());
            nombre_producto.setText(datos.getNombreProducto());
            descripcion_producto.setText(datos.getDescripcionProducto());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Intent intent = new Intent(itemView.getContext(),MainActivity.class);
                    //intent.putExtra("productos",datos);
                    //itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
