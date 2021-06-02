package com.desarollo.salvavidasapp.ui.direction;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.sales.addProduct;

import java.util.ArrayList;
/*
    Adaptador del RecyclerView de la lista de direcciones (controlador)
 */
public class ListAddressAdapter extends RecyclerView.Adapter<ListAddressAdapter.ViewHolder> implements View.OnClickListener{

    ArrayList<ListDirecciones> model;
    LayoutInflater inflater;
    String seleccion = "";

    //listener
    private View.OnClickListener listener;

    public ListAddressAdapter(Context context, ArrayList<ListDirecciones> model){
            this.inflater = LayoutInflater.from(context);
            this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lista_direcciones,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = model.get(position).getNombreDireccion();
        String direccion = model.get(position).getDireccionUsuario();
        String municipio = model.get(position).getMunicipioDireccion();
        seleccion = model.get(position).getSeleccion();

        int imagen = model.get(position).getImagenID();
        holder.nombre.setText(nombre);
        holder.direccion.setText(direccion);
        holder.municipio.setText(municipio);
        holder.imagenDireccion.setImageResource(imagen);
        if(seleccion.equals("true")){
            holder.imagenSeleccion.setImageResource(R.drawable.ok);
        }


        holder.imagenDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (seleccion.equals("true")) {

                   seleccion="false";
                   holder.imagenSeleccion.setImageResource(0);
               }else{
                   seleccion="true";
                   holder.imagenSeleccion.setImageResource(R.drawable.ok);
               }
            }
        });
    }



    @Override
    public int getItemCount() {
        int a ;
        if(model != null && !model.isEmpty()) {
            a = model.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, direccion, municipio;
        ImageView imagenDireccion,imagenSeleccion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.NombreDireccion);
            direccion = itemView.findViewById(R.id.Direccion);
            municipio = itemView.findViewById(R.id.municipioDireccion);
            imagenDireccion = itemView.findViewById(R.id.Imagen_direccion);
            imagenSeleccion = itemView.findViewById(R.id.Imagen_seleccion);

        }
    }
}
