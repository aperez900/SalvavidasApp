package com.desarollo.salvavidasapp.ui.sales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desarollo.salvavidasapp.Models.Productos;
import com.desarollo.salvavidasapp.R;
import com.desarollo.salvavidasapp.ui.home.ListSellAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListSaleAdapter extends RecyclerView.Adapter<ListSaleAdapter.viewHolder>  implements View.OnClickListener  {

    ArrayList<Productos> listaDeDatos;
    LayoutInflater inflater;
    private View.OnClickListener listener;
    Activity  activity;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;


    public ListSaleAdapter(Context context, ArrayList<Productos> listaDeDatos, Activity activity) {
        this.inflater = LayoutInflater.from(context);
        this.listaDeDatos = listaDeDatos;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ListSaleAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_de_lista_crud,null,false);
        view.setOnClickListener(this);
        return new ListSaleAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSaleAdapter.viewHolder holder, int position) {

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

        Glide.with(activity)
                .load(getUrlFoto)
                .into(holder.imagenProducto);


        //Acción del botón ver mas
        holder.imgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity , addProduct2.class);
                intent.putExtra("nombreProducto", listaDeDatos.get(position).getNombreProducto());
                intent.putExtra("idProducto" , listaDeDatos.get(position).getIdProducto());
                intent.putExtra("tipoProducto" , listaDeDatos.get(position).getCategoriaProducto());
                intent.putExtra("domicilioProducto" , listaDeDatos.get(position).getDomicilio());
                intent.putExtra("descripcionProducto" , listaDeDatos.get(position).getDescripcionProducto());
                intent.putExtra("precio" , String.valueOf(listaDeDatos.get(position).getPrecio()));
                intent.putExtra("descuento" , String.valueOf(listaDeDatos.get(position).getDescuento()));
                intent.putExtra("fechaInicio", listaDeDatos.get(position).getFechaInicio());
                intent.putExtra("horaInicio", listaDeDatos.get(position).getHoraInicio());
                intent.putExtra("fechaFin", listaDeDatos.get(position).getFechaFin());
                intent.putExtra("horaFin", listaDeDatos.get(position).getHoraFin());
                intent.putExtra("getUrlFoto" , listaDeDatos.get(position).getfoto());
                intent.putExtra("tipyEntry" , "Consultar");

                activity.startActivity(intent);
            }
        });


        //Acción del botón Editar
        holder.imgEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity , addProduct2.class);
                intent.putExtra("nombreProducto", listaDeDatos.get(position).getNombreProducto());
                intent.putExtra("idProducto" , listaDeDatos.get(position).getIdProducto());
                intent.putExtra("tipoProducto" , listaDeDatos.get(position).getCategoriaProducto());
                intent.putExtra("domicilioProducto" , listaDeDatos.get(position).getDomicilio());
                intent.putExtra("descripcionProducto" , listaDeDatos.get(position).getDescripcionProducto());
                intent.putExtra("precio" , String.valueOf(listaDeDatos.get(position).getPrecio()));
                intent.putExtra("descuento" , String.valueOf(listaDeDatos.get(position).getDescuento()));
                intent.putExtra("fechaInicio", listaDeDatos.get(position).getFechaInicio());
                intent.putExtra("horaInicio", listaDeDatos.get(position).getHoraInicio());
                intent.putExtra("fechaFin", listaDeDatos.get(position).getFechaFin());
                intent.putExtra("horaFin", listaDeDatos.get(position).getHoraFin());
                intent.putExtra("getUrlFoto" , listaDeDatos.get(position).getfoto());
                intent.putExtra("tipyEntry" , "Editar");

                activity.startActivity(intent);

            }
        });

        //Acción del botón Cancelar
        holder.imgCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("productos");

                myRef.child(currentUser.getUid()).child(id_producto).child("estadoProducto").setValue("Cancelado")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, "Producto cancelado con éxito", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Error cancelando el producto", Toast.LENGTH_SHORT).show();
                            }
                        });
                // Toast.makeText(getApplicationContext(),"Clic en Cancelar: " + nombre_producto,Toast.LENGTH_SHORT).show();
            }
        });

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
        TextView tipo_producto, nombre_producto, descripcion_producto, precio, fechaInicio, fechaFin;
        ImageView imagenProducto, imgVer, imgEditar, imgCancelar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
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


    }
}
