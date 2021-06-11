package com.desarollo.salvavidasapp.ui.favorites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.SubTipoComidas;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.look_at_address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
    Adaptador del RecyclerView de la lista de direcciones (controlador)
 */
public class ListFavoritesAdapter extends RecyclerView.Adapter<ListFavoritesAdapter.ViewHolder> implements View.OnClickListener{

    ArrayList<SubTipoComidas> model;
    LayoutInflater inflater;
    Activity activity;

    //listener
    private View.OnClickListener listener;

    public ListFavoritesAdapter(Context context, ArrayList<SubTipoComidas> model, Activity activity){
            this.inflater = LayoutInflater.from(context);
            this.model = model;
            this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_favoritos,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String subTipo = model.get(position).getSubTipoComida();
        String imagen = model.get(position).getFoto();


        holder.subTipo.setText(subTipo);

        Glide.with(activity)
                .load(imagen)
                .into(holder.imagen_fav);

        Map m = new HashMap();
       holder.ch_fav.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (((CheckBox)v).isChecked()) {
                   m.put(subTipo, "True");
               }else{
                for(int i = 0;i<model.size();i++){
                    if(subTipo.equals(m.containsKey(subTipo))){
                        m.remove(subTipo);
                    }
                 }
               }
           }
       });

       holder.btnFavoritos.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {


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
        TextView subTipo;
        ImageView imagen_fav;
        CheckBox ch_fav;
        Button btnFavoritos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTipo = itemView.findViewById(R.id.tv_nombre_fav_sub_tipo);
            imagen_fav = itemView.findViewById(R.id.img_imagen_fav_producto);
            ch_fav = itemView.findViewById(R.id.ch_favorito);
            btnFavoritos = itemView.findViewById(R.id.btnAgregarFavoritos);


        }
    }
}
