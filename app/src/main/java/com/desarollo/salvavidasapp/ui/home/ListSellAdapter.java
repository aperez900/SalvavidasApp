package com.desarollo.salvavidasapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.ListAddressAdapter;

import java.util.ArrayList;

public class ListSellAdapter extends RecyclerView.Adapter<ListSellAdapter.viewHolder> implements View.OnClickListener {

    ArrayList<Productos> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;

    public ListSellAdapter(Context context, ArrayList<Productos> listaDeDatos) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_de_lista,parent,false);
        view.setOnClickListener(this);
        return new ListSellAdapter.viewHolder(view);

    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }


    @Override

    public void onBindViewHolder(@NonNull ListSellAdapter.viewHolder holder, int position) {
        String tipo_producto = listaDeDatos.get(position).getCategoriaProducto();
        String nombre_producto = listaDeDatos.get(position).getNombreProducto();
        String descripcion_producto = listaDeDatos.get(position).getDescripcionProducto();

        holder.tipo_producto.setText(tipo_producto);
        holder.nombre_producto.setText(nombre_producto);
        holder.descripcion_producto.setText(descripcion_producto);
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
    public void onClick(View v) {

    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tipo_producto,nombre_producto,descripcion_producto;
        ImageView imagenProducto;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tipo_producto = itemView.findViewById(R.id.tipo_producto);
            nombre_producto = itemView.findViewById(R.id.et_nombre_producto);
            descripcion_producto = itemView.findViewById(R.id.et_descripcion_producto);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);

        }



    }
}
