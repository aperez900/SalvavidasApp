package com.desarollo.salvavidasapp.ui.sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;

import java.util.ArrayList;

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
            tipo_producto = itemView.findViewById(R.id.tipo_producto);
            nombre_producto = itemView.findViewById(R.id.et_nombre_producto);
            descripcion_producto = itemView.findViewById(R.id.et_descripcion_producto);
           // imagenProducto = itemView.findViewById(R.id.imagenProducto);

        }

        public void actualizarDatosDeItem(Productos datos) {

            tipo_producto.setText(datos.getCategoriaProducto());
            nombre_producto.setText(datos.getNombreProducto());
            descripcion_producto.setText(datos.getDescripcionProducto());
            //imagenProducto.setImageResource(datos.getFoto());

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
