package com.desarollo.salvavidasapp.ui.home;

import android.annotation.SuppressLint;
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
import com.desarollo.salvavidasapp.Models.TipoProductos;
import com.desarollo.salvavidasapp.R;

import java.util.ArrayList;

public class ListTypeFood  extends RecyclerView.Adapter<ListTypeFood.viewHolder> implements View.OnClickListener{
    ArrayList<TipoProductos> listaDeDatos;
    LayoutInflater inflater;

    private View.OnClickListener listener;
    Activity activity;


    public ListTypeFood(Context context, ArrayList<TipoProductos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ListTypeFood.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_tipo_comidas,null,false);
        view.setOnClickListener(this);
        return new ListTypeFood.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListTypeFood.viewHolder holder, @SuppressLint("RecyclerView") int position) {

        String TipoComida = listaDeDatos.get(position).getTipoComida();
        String getUrlFoto = listaDeDatos.get(position).getFoto();

        holder.TipoComida.setText(TipoComida);
        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenTipoComida);


        holder.imagenTipoComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(activity , addProduct.class);
                Intent intent = new Intent(activity , productsByType.class);
                intent.putExtra("TipoComida", listaDeDatos.get(position).getTipoComida());
                intent.putExtra("foto" , listaDeDatos.get(position).getFoto());

                activity.startActivity(intent);
            }
        });

    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
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
        TextView TipoComida;
        ImageView imagenTipoComida;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            TipoComida = itemView.findViewById(R.id.tipo_comidas);
            imagenTipoComida = itemView.findViewById(R.id.img_imagen_tipo);
        }
    }
}
