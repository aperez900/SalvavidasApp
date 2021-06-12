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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Favoritos;
import com.desarollo.salvavidasapp.Models.ListDirecciones;
import com.desarollo.salvavidasapp.Models.SubTipoComidas;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.direction.look_at_address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/*
    Adaptador del RecyclerView de la lista de direcciones (controlador)
 */
public class ListFavoritesAdapter extends RecyclerView.Adapter<ListFavoritesAdapter.ViewHolder> implements View.OnClickListener{

    ArrayList<Favoritos> model;
    LayoutInflater inflater;
    Activity activity;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    //listener
    private View.OnClickListener listener;

    public ListFavoritesAdapter(Context context, ArrayList<Favoritos> model, Activity activity){
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

       holder.ch_fav.setChecked(model.get(position).getEstado());

       holder.ch_fav.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (((CheckBox)v).isChecked()) {
                   agregarFavoritos(subTipo);

               }else{
                   eliminarFavoritos(subTipo);

               }
           }
       });


    }


    public void agregarFavoritos(String idProducto){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios").child(currentUser.getUid()).child("comidas_preferidas").child(idProducto);
        myRef.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
            }
        });

        /*Intent intent = new Intent(activity , shoppingCart.class);
        activity.startActivity(intent);
         */
    }




    public void eliminarFavoritos(String idProducto){

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios").child(currentUser.getUid()).child("comidas_preferidas").child(idProducto);
        myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error. Intenta de nuevo mas tarde", Toast.LENGTH_SHORT).show();
            }
        });

        /*Intent intent = new Intent(activity , shoppingCart.class);
        activity.startActivity(intent);
         */
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subTipo = itemView.findViewById(R.id.tv_nombre_fav_sub_tipo);
            imagen_fav = itemView.findViewById(R.id.img_imagen_fav_producto);
            ch_fav = itemView.findViewById(R.id.ch_favorito);


        }
    }
}
