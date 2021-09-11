package com.desarollo.salvavidasapp.ui.direction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.R;

import java.util.ArrayList;
/*
    Adaptador del RecyclerView de la lista de direcciones (controlador)
 */
public class ListAddressAdapter extends RecyclerView.Adapter<ListAddressAdapter.ViewHolder> implements View.OnClickListener{

    ArrayList<ListDirecciones> model;
    LayoutInflater inflater;
    String seleccion = "";
    Activity activity;

    //listener
    private View.OnClickListener listener;

    public ListAddressAdapter(Context context, ArrayList<ListDirecciones> model,Activity activity){
            this.inflater = LayoutInflater.from(context);
            this.model = model;
            this.activity = activity;
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


        holder.nombre.setText(nombre);
        holder.direccion.setText(direccion);
        holder.municipio.setText(municipio);

        int imagen = model.get(position).getImagenID();
        holder.imagenDireccion.setImageResource(imagen);
        if(seleccion.equals("true")){
            holder.direccion_principal.setChecked(true);
        }
        else{
            holder.direccion_principal.setChecked(false);
        }


        holder.direccion_principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });


        holder.imagenDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , lookAtAddress.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("direccion" , direccion);
                intent.putExtra("municipio" , municipio);

                activity.startActivity(intent);

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
        RadioButton direccion_principal;
        ImageView imagenDireccion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.NombreDireccion);
            direccion = itemView.findViewById(R.id.Direccion);
            municipio = itemView.findViewById(R.id.municipioDireccion);
            imagenDireccion = itemView.findViewById(R.id.Imagen_direccion);
            direccion_principal = itemView.findViewById(R.id.direccion_principal);

        }
    }
}
