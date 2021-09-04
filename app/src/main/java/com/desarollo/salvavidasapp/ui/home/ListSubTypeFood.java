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
import com.desarollo.salvavidasapp.Models.SubTipoComidas;
import com.desarollo.salvavidasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListSubTypeFood extends RecyclerView.Adapter<ListSubTypeFood.viewHolder> implements View.OnClickListener{
    ArrayList<SubTipoComidas> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity activity;


    public ListSubTypeFood(Context context, ArrayList<SubTipoComidas> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_sub_tipo_comidas,null,false);
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        String getUrlFoto = listaDeDatos.get(position).getFoto();
        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenTipoComida);


        holder.imagenTipoComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(activity , addProduct.class);
                String SubTipoComida = listaDeDatos.get(position).getSubTipoComida();
                String TipoProducto = listaDeDatos.get(position).getTipo();
                Intent intent = new Intent(activity , products_by_sub_type.class);
                intent.putExtra("SubTipoComida", SubTipoComida);
                intent.putExtra("TipoProducto", TipoProducto);

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
